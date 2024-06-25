package com.rfdotech.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.ZonedDateTimeHelper
import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.notification.ActiveRunService
import com.rfdotech.core.presentation.ui.asUiText
import com.rfdotech.run.domain.RunningTracker
import com.rfdotech.run.domain.WatchConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val watchConnector: WatchConnector,
    private val applicationScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    var state by mutableStateOf(
        ActiveRunState(
            shouldTrack = ActiveRunService.isServiceActive.value && runningTracker.isTracking.value,
            hasStartedRunning = ActiveRunService.isServiceActive.value
        )
    )
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val shouldTrack = snapshotFlow { state.shouldTrack }
        .stateIn(viewModelScope, SharingStarted.Lazily, state.shouldTrack)
    private val hasLocationPermission = MutableStateFlow(false)

    private val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ) { shouldTrack, hasLocationPermission ->
        shouldTrack && hasLocationPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        hasLocationPermission
            .onEach { hasPermission ->
                if (hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        isTracking.onEach { isTracking ->
            runningTracker.setIsTracking(isTracking)
        }.launchIn(viewModelScope)

        runningTracker.currentLocation.onEach {
            state = state.copy(currentLocation = it?.location)
        }.launchIn(viewModelScope)

        runningTracker.runData.onEach {
            state = state.copy(runData = it)
        }.launchIn(viewModelScope)

        runningTracker.elapsedTime.onEach {
            state = state.copy(elapsedTime = it)
        }.launchIn(viewModelScope)

        runningTracker.stepCount.onEach {
            state = state.copy(stepCount = it)
        }.launchIn(viewModelScope)

        listenToWatchActions()
    }

    fun onAction(action: ActiveRunAction, triggeredOnWatch: Boolean = false) {
        if (!triggeredOnWatch) {
            sendActionToWatch(action)
        }
        when (action) {
            ActiveRunAction.OnFinishRunClick -> {
                state = state.copy(isRunFinished = true, isSavingRun = true)
            }

            ActiveRunAction.OnResumeRunClick -> {
                state = state.copy(shouldTrack = true)
            }

            ActiveRunAction.OnBackClick -> {
                state = state.copy(shouldTrack = false)
            }

            ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }

            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.update { action.acceptedPermission }
                state = state.copy(
                    showLocationRationale = action.shouldShowRationale
                )
            }

            is ActiveRunAction.SubmitPostNotificationPermissionInfo -> {
                state = state.copy(
                    showPostNotificationRationale = action.shouldShowRationale
                )
            }

            ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showLocationRationale = false,
                    showPostNotificationRationale = false
                )
            }

            is ActiveRunAction.OnRunProcessed -> {
                finishRun(action.mapPictureBytes)
            }
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) = viewModelScope.launch {
        val locations = state.runData.locations
        if (locations.isEmpty() || locations.first().size <= 1) {
            state = state.copy(isSavingRun = false)
            eventChannel.send(ActiveRunEvent.InvalidRunDiscarded)
            return@launch
        }
        val maxSpeedKmh = async(dispatcherProvider.io) {
            DistanceAndSpeedCalculator.getMaxSpeedKmh(locations)
        }
        val totalElevationMeters = async(dispatcherProvider.io) {
            DistanceAndSpeedCalculator.getTotalElevationMeters(locations)
        }
        val avgHeartRate = if (state.runData.heartRates.isNotEmpty()) {
            state.runData.heartRates.average().roundToInt()
        } else {
            0
        }

        val run = Run(
            id = null,
            duration = state.elapsedTime,
            dateTimeUtc = ZonedDateTimeHelper.addZoneIdToZonedDateTime(ZonedDateTime.now()),
            distanceMeters = state.runData.distanceMeters,
            location = state.currentLocation ?: Location(0.0, 0.0),
            maxSpeedKmh = maxSpeedKmh.await(),
            totalElevationMeters = totalElevationMeters.await(),
            numberOfSteps = state.stepCount,
            avgHeartRate = avgHeartRate,
            mapPictureUrl = null
        )
        runningTracker.finishRun()

        when (
            val result = withContext(dispatcherProvider.io) {
                runRepository.upsert(run, mapPictureBytes)
            }
        ) {
            is Result.Error -> {
                eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
            }

            is Result.Success -> {
                eventChannel.send(ActiveRunEvent.RunSaved)
            }
        }

        state = state.copy(isSavingRun = false)
    }

    private fun sendActionToWatch(action: ActiveRunAction) = viewModelScope.launch {
        val messagingAction = when (action) {
            ActiveRunAction.OnFinishRunClick -> MessagingAction.Finish
            ActiveRunAction.OnResumeRunClick -> MessagingAction.StartOrResumeRun
            ActiveRunAction.OnToggleRunClick -> {
                if (state.hasStartedRunning) {
                    MessagingAction.Pause
                } else {
                    MessagingAction.StartOrResumeRun
                }
            }

            else -> null
        }

        messagingAction?.let {
            watchConnector.sendActionToWatch(it)
        }
    }

    private fun listenToWatchActions() {
        watchConnector
            .messagingActions
            .onEach { action ->
                when (action) {
                    MessagingAction.ConnectionRequest -> {
                        if (isTracking.value) {
                            watchConnector.sendActionToWatch(MessagingAction.StartOrResumeRun)
                        }
                    }

                    MessagingAction.Finish -> {
                        onAction(ActiveRunAction.OnFinishRunClick, true)
                    }

                    MessagingAction.Pause -> {
                        if (isTracking.value) {
                            onAction(ActiveRunAction.OnToggleRunClick, true)
                        }
                    }

                    MessagingAction.StartOrResumeRun -> {
                        if (!isTracking.value) {
                            if (state.hasStartedRunning) {
                                onAction(ActiveRunAction.OnResumeRunClick, true)
                            } else {
                                onAction(ActiveRunAction.OnToggleRunClick, true)
                            }
                        }
                    }

                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        if (!ActiveRunService.isServiceActive.value) {
            applicationScope.launch {
                // This is an edge case where the user closes the app while in the active run screen but decided not to start a new run.
                // If we don't do this, the start and finish button will still be visible in the watch.
                watchConnector.sendActionToWatch(MessagingAction.NotTrackable)
            }
            runningTracker.stopObservingLocation()
        }
    }
}