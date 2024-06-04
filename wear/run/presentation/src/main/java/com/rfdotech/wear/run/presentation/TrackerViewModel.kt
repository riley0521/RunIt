@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.rfdotech.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.notification.ActiveRunService
import com.rfdotech.wear.run.domain.ExerciseTracker
import com.rfdotech.wear.run.domain.PhoneConnector
import com.rfdotech.wear.run.domain.RunningTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker,
    private val phoneConnector: PhoneConnector,
    private val runningTracker: RunningTracker
) : ViewModel() {

    var state by mutableStateOf(
        TrackerState(
            hasStartedRunning = ActiveRunService.isServiceActive.value,
            isTrackable = ActiveRunService.isServiceActive.value,
            isRunActive = ActiveRunService.isServiceActive.value && runningTracker.isTracking.value
        )
    )
        private set

    private val hasBodySensorPermission = MutableStateFlow(false)
    private val isTracking = snapshotFlow {
        state.isRunActive && state.isTrackable && state.isConnectedPhoneNearby
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val eventChannel = Channel<TrackerEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        phoneConnector
            .connectedDevice
            .filterNotNull()
            .onEach { device ->
                Timber.tag("CONNECTED_DEVICE").d(device.toString())
                state = state.copy(isConnectedPhoneNearby = device.isNearby)
            }
            .combine(isTracking) { _, isTracking ->
                if (!isTracking) {
                    phoneConnector.sendActionToPhone(MessagingAction.ConnectionRequest)
                }
            }
            .launchIn(viewModelScope)

        runningTracker
            .isTrackable
            .onEach {
                state = state.copy(isTrackable = it)
            }
            .launchIn(viewModelScope)

        observeIsTrackingAndSetExerciseStatus()
        observeValuesInAmbientMode()
        listenToPhoneActions()
    }

    private fun observeIsTrackingAndSetExerciseStatus() {
        isTracking
            .onEach { isTracking ->
                val result = when {
                    isTracking && !state.hasStartedRunning -> {
                        exerciseTracker.startExercise()
                    }

                    isTracking && state.hasStartedRunning -> {
                        exerciseTracker.resumeExercise()
                    }

                    !isTracking && state.hasStartedRunning -> {
                        exerciseTracker.pauseExercise()
                    }

                    else -> Result.Success(Unit)
                }

                if (result is Result.Error) {
                    result.error.toUiText()?.let { text ->
                        eventChannel.send(TrackerEvent.Error(text))
                    }
                }

                if (isTracking) {
                    state = state.copy(hasStartedRunning = true)
                }
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)
    }

    private fun observeValuesInAmbientMode() {
        val isAmbientMode = snapshotFlow { state.isAmbientMode }
        val interval = 10.seconds

        isAmbientMode
            .flatMapLatest {
                if (it) {
                    getFlowOfDistanceAndElapsedTime().sample(interval)
                } else {
                    getFlowOfDistanceAndElapsedTime()
                }
            }.onEach { (distanceMeters, elapsedTime) ->
                state = state.copy(
                    distanceMeters = distanceMeters,
                    elapsedTime = elapsedTime
                )
            }.launchIn(viewModelScope)

        isAmbientMode
            .flatMapLatest {
                if (it) {
                    runningTracker.heartRate.sample(interval)
                } else {
                    runningTracker.heartRate
                }
            }.onEach {
                state = state.copy(heartRate = it)
            }.launchIn(viewModelScope)
    }

    private fun getFlowOfDistanceAndElapsedTime(): Flow<Pair<Int, Duration>> {
        return combine(
            runningTracker.distanceMeters,
            runningTracker.elapsedTime
        ) { distanceMeters, elapsedTime ->
            Pair(distanceMeters, elapsedTime)
        }
    }

    fun onAction(action: TrackerAction, triggeredOnPhone: Boolean = false) {
        if (!triggeredOnPhone) {
            sendActionToPhone(action)
        }
        when (action) {
            TrackerAction.OnFinishRunClick -> {
                viewModelScope.launch {
                    exerciseTracker.stopExercise()
                    eventChannel.send(TrackerEvent.RunFinished)

                    state = state.copy(
                        elapsedTime = Duration.ZERO,
                        distanceMeters = 0,
                        heartRate = 0,
                        hasStartedRunning = false,
                        isRunActive = false
                    )
                }
            }

            TrackerAction.OnToggleRunClick -> {
                viewModelScope.launch {
                    if (state.isTrackable) {
                        state = state.copy(
                            isRunActive = !state.isRunActive
                        )
                    }
                }
            }

            is TrackerAction.OnBodySensorPermissionResult -> {
                hasBodySensorPermission.update { action.isGranted }
                if (action.isGranted) {
                    viewModelScope.launch {
                        val isSupported = exerciseTracker.isHeartRateTrackingSupported()
                        state = state.copy(
                            canTrackHeartRate = isSupported
                        )
                    }
                }
            }

            is TrackerAction.OnEnterAmbientMode -> {
                state = state.copy(
                    isAmbientMode = true,
                    burnInProtectionRequired = action.burnInProtectionRequired
                )
            }
            TrackerAction.OnExitAmbientMode -> {
                state = state.copy(
                    isAmbientMode = false
                )
            }
        }
    }

    private fun sendActionToPhone(action: TrackerAction) = viewModelScope.launch {
        val messagingAction = when (action) {
            TrackerAction.OnFinishRunClick -> MessagingAction.Finish
            TrackerAction.OnToggleRunClick -> {
                if (state.isRunActive) {
                    MessagingAction.Pause
                } else {
                    MessagingAction.StartOrResumeRun
                }
            }

            else -> null
        }

        messagingAction?.let {
            val result = phoneConnector.sendActionToPhone(it)
            if (result is Result.Error) {
                Timber.tag("SEND_ACTION_ERROR").e(result.error.name)
            }
        }
    }

    private fun listenToPhoneActions() {
        phoneConnector
            .messagingActions
            .onEach { action ->
                when (action) {
                    MessagingAction.Finish -> {
                        onAction(TrackerAction.OnFinishRunClick, true)
                    }

                    MessagingAction.Pause -> {
                        if (state.isTrackable) {
                            state = state.copy(isRunActive = false)
                        }
                    }

                    MessagingAction.StartOrResumeRun -> {
                        if (state.isTrackable) {
                            state = state.copy(isRunActive = true)
                        }
                    }

                    MessagingAction.Trackable -> {
                        state = state.copy(isTrackable = true)
                    }

                    MessagingAction.NotTrackable -> {
                        state = state.copy(isTrackable = false)
                    }

                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }
}