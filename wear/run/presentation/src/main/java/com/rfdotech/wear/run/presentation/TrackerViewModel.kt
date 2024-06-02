package com.rfdotech.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TrackerViewModel : ViewModel() {

    var state by mutableStateOf(TrackerState())
        private set

    fun onAction(action: TrackerAction) {
        when (action) {
            TrackerAction.OnFinishRunClick -> TODO()
            TrackerAction.OnToggleRunClick -> TODO()
        }
    }
}