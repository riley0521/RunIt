package com.rfdotech.runtracker

sealed interface MainAction {
    data object OnAuthenticationExpired: MainAction
}