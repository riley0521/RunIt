package com.rfdotech.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class ActiveRunViewModel(
    private val runningTracker: RunningTracker
) : ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _hasLocationPermission = MutableStateFlow(false)

    init {
        _hasLocationPermission
            .onEach { hasPermission ->
                if (hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ActiveRunAction) {
        when (action) {
            ActiveRunAction.OnFinishRunClick -> {}
            ActiveRunAction.OnResumeRunClick -> {}
            ActiveRunAction.OnToggleRunClick -> {}
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                _hasLocationPermission.update { action.acceptedPermission }
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
            else -> Unit
        }
    }
}