package com.rfdotech.auth.presentation.intro

sealed interface IntroEvent {
    data object OnSignInSuccessful : IntroEvent
}