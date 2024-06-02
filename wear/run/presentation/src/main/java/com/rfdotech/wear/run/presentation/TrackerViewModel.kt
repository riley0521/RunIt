package com.rfdotech.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.wear.run.domain.ExerciseTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker
) : ViewModel() {

    var state by mutableStateOf(TrackerState())
        private set

    private val hasBodySensorPermission = MutableStateFlow(false)

    init {
        exerciseTracker.heartRate.onEach { heartRate ->
            Timber.tag("HEART_RATE").d("$heartRate bpm")
            state = state.copy(heartRate = heartRate)
        }.launchIn(viewModelScope)
    }

    fun onAction(action: TrackerAction) {
        when (action) {
            TrackerAction.OnFinishRunClick -> Unit
            TrackerAction.OnToggleRunClick -> {
                viewModelScope.launch {
                    if (!state.hasStartedRunning) {
                        exerciseTracker.startExercise()
                    }

                    state = state.copy(
                        hasStartedRunning = true,
                        isRunActive = !state.isRunActive
                    )
                }
            }
            is TrackerAction.OnBodySensorPermissionResult -> {
                hasBodySensorPermission.update { action.isGranted }
                if (action.isGranted) {
                    viewModelScope.launch {
                        val isHeartRateTrackingSupported = exerciseTracker
                            .isHeartRateTrackingSupported()
                        state = state.copy(canTrackHeartRate = isHeartRateTrackingSupported)

                        // Prepare exercise
                        exerciseTracker.prepareExercise()

                        // TODO: Remove after testing
                        exerciseTracker.startExercise()
                    }
                }
            }
        }
    }
}