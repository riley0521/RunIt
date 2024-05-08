package com.rfdotech.run.presentation.run_overview

sealed interface RunOverviewAction {
    data object OnStartClick: RunOverviewAction
    data object OnSignOutClick: RunOverviewAction
    data object OnAnalyticsClick: RunOverviewAction
}