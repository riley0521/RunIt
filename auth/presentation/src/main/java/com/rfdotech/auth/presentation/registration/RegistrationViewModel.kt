@file:Suppress("OPT_IN_USAGE_FUTURE_ERROR")

package com.rfdotech.auth.presentation.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegistrationViewModel(

): ViewModel() {

    var state by mutableStateOf(RegistrationState())
        private set

    fun onAction(action: RegistrationAction) {
        when (action) {
            RegistrationAction.OnSignInClick -> TODO()
            RegistrationAction.OnSignUpClick -> TODO()
            RegistrationAction.OnTogglePasswordVisibilityClick -> TODO()
        }
    }
}