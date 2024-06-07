package com.rfdotech.run.domain

import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.domain.Timer
import com.rfdotech.core.domain.location.LocationTimestamp
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import java.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class RunningTracker(
    private val locationObserver: LocationObserver,
    private val stepObserver: StepObserver,
    private val watchConnector: WatchConnector,
    applicationScope: CoroutineScope,
    private val clock: Clock = Clock.systemDefaultZone()
) {

    companion object {
        private const val OBSERVE_LOCATION_INTERVAL = 1000L
    }

    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    val currentLocation = isObservingLocation
        .flatMapLatest {
            if (it) {
                locationObserver.observeLocation(OBSERVE_LOCATION_INTERVAL)
            } else flowOf()
        }
        .stateIn(applicationScope, SharingStarted.Lazily, null)

    private val _stepCount = MutableStateFlow(0)
    val stepCount = _stepCount.asStateFlow()

    private val heartRates: StateFlow<List<Int>> = isTracking
        .flatMapLatest { isTracking ->
            if (isTracking) {
                watchConnector.messagingActions
            } else flowOf()
        }
        .filterIsInstance<MessagingAction.HeartRateUpdate>()
        .map { it.heartRate }
        .runningFold(initial = emptyList<Int>()) { currentHeartRates, newHeartRate ->
            currentHeartRates + newHeartRate
        }
        .stateIn(applicationScope, SharingStarted.Lazily, emptyList())

    init {
        _isTracking
            .onEach { isTracking ->
                if (!isTracking) {
                    addBreakToListOfLocations()
                }
            }
            .flatMapLatest {
                if (it) {
                    Timer.timeAndEmit(clock)
                } else flowOf()
            }.onEach { value ->
                _elapsedTime.update { it + value }
            }
            .launchIn(applicationScope)

        _isTracking.flatMapLatest {
            if (it) {
                stepObserver.observeSteps(_stepCount.value)
            } else flowOf()
        }.onEach { newStepCount ->
            _stepCount.update { newStepCount }
        }.launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(_isTracking) { location, isTracking ->
                if (isTracking) {
                    emit(location)
                }
            }.zip(_elapsedTime) { location, elapsedTime ->
                LocationTimestamp(
                    location = location,
                    durationTimestamp = elapsedTime
                )
            }.combine(heartRates) { location, heartRates ->
                convertLocationWithTimestampToRunData(location, heartRates)
            }
            .launchIn(applicationScope)

        elapsedTime
            .onEach {
                watchConnector.sendActionToWatch(MessagingAction.TimeUpdate(it))
            }
            .launchIn(applicationScope)

        runData
            .map { it.distanceMeters }
            .distinctUntilChanged()
            .onEach { distanceMeters ->
                watchConnector.sendActionToWatch(MessagingAction.DistanceUpdate(distanceMeters))
            }
            .launchIn(applicationScope)
    }

    private fun addBreakToListOfLocations() {
        val newList: ListOfLocations = buildList {
            addAll(runData.value.locations)
            add(emptyList<LocationTimestamp>())
        }.toList()
        _runData.update { it.copy(locations = newList) }
    }

    fun setIsTracking(isTracking: Boolean) {
        this._isTracking.update { isTracking }
    }

    fun startObservingLocation() {
        isObservingLocation.update { true }
        watchConnector.setIsTrackable(true)
    }

    fun stopObservingLocation() {
        isObservingLocation.update { false }
        watchConnector.setIsTrackable(false)
    }

    fun finishRun() {
        stopObservingLocation()
        setIsTracking(false)
        _elapsedTime.update { Duration.ZERO }
        _runData.update { RunData() }
        _stepCount.update { 0 }
    }

    private fun convertLocationWithTimestampToRunData(location: LocationTimestamp, heartRates: List<Int>) {
        val currentLocations = runData.value.locations
        val lastLocationsList = if (currentLocations.isNotEmpty()) {
            currentLocations.last() + location
        } else {
            listOf(location)
        }
        val newLocationsList = currentLocations.replaceLast(lastLocationsList)
        val distanceMeters = LocationDataCalculator.getTotalDistanceInMeters(newLocationsList)
        val distanceKm = DistanceAndSpeedCalculator.getKmFromMeters(distanceMeters)
        val currentDuration = location.durationTimestamp

        val avgSecondsPerKm = DistanceAndSpeedCalculator.getSecondsPerKm(distanceKm, currentDuration)

        _runData.update {
            RunData(
                distanceMeters = distanceMeters,
                paceInSeconds = avgSecondsPerKm.seconds,
                locations = newLocationsList,
                heartRates = heartRates
            )
        }
    }

    /**
     * Check if the app glitches and registers a location with huge gap that is more than 10 meters
     */
    private fun isNotValidDistance(locations: ListOfLocations, locationTimestamp: LocationTimestamp): Boolean {
        val location1 = locations.last().lastOrNull()?.location?.location ?: return false
        val location2 = locationTimestamp.location.location

        return location1.distanceTo(location2) > 10
    }

    private fun ListOfLocations.replaceLast(replacement: List<LocationTimestamp>): ListOfLocations {
        if (this.isEmpty()) {
            return listOf(replacement)
        }
        return this.dropLast(1) + listOf(replacement)
    }
}