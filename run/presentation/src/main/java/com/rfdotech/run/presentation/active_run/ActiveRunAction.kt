package com.rfdotech.run.presentation.active_run

sealed interface ActiveRunAction {
    data object OnToggleRunClick: ActiveRunAction
    data object OnFinishRunClick: ActiveRunAction
    data object OnResumeRunClick: ActiveRunAction
    data object OnBackClick: ActiveRunAction
    data class SubmitLocationPermissionInfo(
        val acceptedPermission: Boolean,
        val shouldShowRationale: Boolean
    ): ActiveRunAction

    data class SubmitPostNotificationPermissionInfo(
        val acceptedPermission: Boolean,
        val shouldShowRationale: Boolean
    ): ActiveRunAction

    data object DismissRationaleDialog: ActiveRunAction
}