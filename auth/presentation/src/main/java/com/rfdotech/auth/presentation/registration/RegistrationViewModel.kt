@file:OptIn(ExperimentalFoundationApi::class)

package com.rfdotech.auth.presentation.registration

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.auth.domain.UserDataValidator
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip

class RegistrationViewModel(
    private val userDataValidator: UserDataValidator
): ViewModel() {

    var state by mutableStateOf(RegistrationState())
        private set

    init {
        val emailFlow = state.email.textAsFlow()
        val passwordFlow = state.password.textAsFlow()

        combine(emailFlow, passwordFlow) { email, password ->
            Pair(email, password)
        }.onEach { (email, password) ->
            val isEmailValid = userDataValidator.isValidEmail(email.toString())
            val passwordValidationState = userDataValidator.validatePassword(password.toString())
            val canRegister = passwordValidationState.isValidPassword && !state.isRegistering

            state = state.copy(
                isEmailValid = isEmailValid,
                passwordValidationState = passwordValidationState,
                canRegister = canRegister
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: RegistrationAction) {
        when (action) {
            RegistrationAction.OnSignInClick -> TODO()
            RegistrationAction.OnSignUpClick -> TODO()
            RegistrationAction.OnTogglePasswordVisibilityClick -> TODO()
        }
    }
}