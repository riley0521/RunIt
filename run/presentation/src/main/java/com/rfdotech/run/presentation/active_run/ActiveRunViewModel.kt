package com.rfdotech.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.presentation.ui.asUiText
import com.rfdotech.run.domain.RunningTracker
import com.rfdotech.run.presentation.active_run.service.ActiveRunService
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
import java.time.ZoneId
import java.time.ZonedDateTime

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository
) : ViewModel() {

    var state by mutableStateOf(
        ActiveRunState(
            shouldTrack = ActiveRunService.isServiceActive && runningTracker.isTracking.value,
            hasStartedRunning = ActiveRunService.isServiceActive
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
    }

    fun onAction(action: ActiveRunAction) {
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

        val run = Run(
            id = null,
            duration = state.elapsedTime,
            dateTimeUtc = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")),
            distanceMeters = state.runData.distanceMeters,
            location = state.currentLocation ?: Location(0.0, 0.0),
            maxSpeedKmh = DistanceAndSpeedCalculator.getMaxSpeedKmh(locations),
            totalElevationMeters = DistanceAndSpeedCalculator.getTotalElevationMeters(locations),
            mapPictureUrl = null
        )
        runningTracker.finishRun()

        when (val result = runRepository.upsert(run, mapPictureBytes)) {
            is Result.Error -> {
                eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
            }
            is Result.Success -> {
                eventChannel.send(ActiveRunEvent.RunSaved)
            }
        }

        state = state.copy(isSavingRun = false)
    }

    override fun onCleared() {
        super.onCleared()
        if (!ActiveRunService.isServiceActive) {
            runningTracker.stopObservingLocation()
        }
    }
}