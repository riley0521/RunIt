package com.rfdotech.wear.run.presentation

sealed interface TrackerEvent {
    data object RunFinished: TrackerEvent
}