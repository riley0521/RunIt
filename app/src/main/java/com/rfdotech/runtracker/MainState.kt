package com.rfdotech.runtracker

data class MainState(
    val isSignedIn: Boolean = false,
    val isCheckingAuth: Boolean = false,
    val showAnalyticsInstallDialog: Boolean = false
)
