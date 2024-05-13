package com.rfdotech.run.domain

import com.rfdotech.core.domain.Timer
import com.rfdotech.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class RunningTracker(
    private val locationObserver: LocationObserver,
    applicationScope: CoroutineScope
) {

    companion object {
        private const val OBSERVE_LOCATION_INTERVAL = 1000L
        private const val METERS_PER_KILOMETER = 1000.0
    }

    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    private val isTracking = MutableStateFlow(false)
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

    init {
        isTracking
            .flatMapLatest {
                if (it) {
                    Timer.timeAndEmit()
                } else flowOf()
            }.onEach { value ->
                _elapsedTime.update { it + value }
            }
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(isTracking) { location, isTracking ->
                if (isTracking) {
                    emit(location)
                }
            }.zip(_elapsedTime) { location, elapsedTime ->
                LocationTimestamp(
                    location = location,
                    durationTimestamp = elapsedTime
                )
            }.onEach { location ->
                convertLocationWithTimestampToRunData(location)
            }
            .launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        this.isTracking.update { isTracking }
    }

    fun startObservingLocation() {
        isObservingLocation.update { true }
    }

    fun stopObservingLocation() {
        isObservingLocation.update { false }
    }

    private fun convertLocationWithTimestampToRunData(location: LocationTimestamp) {
        val currentLocations = runData.value.locations
        val lastLocationsList = if (currentLocations.isNotEmpty()) {
            currentLocations.last() + location
        } else {
            listOf(location)
        }
        val newLocationsList = currentLocations.replaceLast(lastLocationsList)
        val distanceMeters = LocationDataCalculator.getTotalDistanceInMeters(newLocationsList)
        val distanceKm = getKmFromMeter(distanceMeters)
        val currentDuration = location.durationTimestamp

        val avgSecondsPerKm = getAvgSecondsPerKm(distanceKm, currentDuration)

        _runData.update {
            RunData(
                distanceMeters = distanceMeters,
                paceInSeconds = avgSecondsPerKm.seconds,
                locations = newLocationsList
            )
        }
    }

    private fun ListOfLocations.replaceLast(replacement: List<LocationTimestamp>): ListOfLocations {
        if (this.isEmpty()) {
            return listOf(replacement)
        }
        return this.dropLast(1) + listOf(replacement)
    }

    private fun getKmFromMeter(meters: Int): Double {
        return meters / METERS_PER_KILOMETER
    }

    private fun getAvgSecondsPerKm(distanceKm: Double, time: Duration): Int {
        return if (distanceKm == 0.0) {
            0
        } else {
            (time.inWholeSeconds / distanceKm).roundToInt()
        }
    }
}