package com.rfdotech.run.presentation.run_overview

sealed interface RunOverviewAction {
    data object OnStartClick: RunOverviewAction
    data object OnSignOutClick: RunOverviewAction
    data object OnAnalyticsClick: RunOverviewAction
    data class DeleteRunById(val id: String): RunOverviewAction
    data object OnDeleteAccountClick: RunOverviewAction
    data class DeleteAccount(val agreed: Boolean): RunOverviewAction
    data object ConfirmDeleteAccount: RunOverviewAction
}