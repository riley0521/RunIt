package com.rfdotech.auth.presentation.sign_in

import com.rfdotech.core.presentation.ui.UiText

sealed interface SignInEvent {
    data class Error(val error: UiText) : SignInEvent
    data object SignInSuccess: SignInEvent
}