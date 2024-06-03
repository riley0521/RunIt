package com.rfdotech.wear.run.presentation

import com.rfdotech.core.presentation.ui.UiText

sealed interface TrackerEvent {
    data object RunFinished: TrackerEvent
    data class Error(val message: UiText): TrackerEvent
}