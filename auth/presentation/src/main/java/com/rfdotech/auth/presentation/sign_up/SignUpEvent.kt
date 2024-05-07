package com.rfdotech.auth.presentation.sign_up

import com.rfdotech.core.presentation.ui.UiText

sealed interface SignUpEvent {
    data object SignUpSuccess: SignUpEvent
    data class Error(val error: UiText): SignUpEvent
}