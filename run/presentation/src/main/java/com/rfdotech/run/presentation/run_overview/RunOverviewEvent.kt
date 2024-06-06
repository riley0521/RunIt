package com.rfdotech.run.presentation.run_overview

sealed interface RunOverviewEvent {
    data object DeleteAccountSuccessful: RunOverviewEvent
    data object NoInternet: RunOverviewEvent
}