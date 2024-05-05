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
        state.email.textAsFlow().onEach {
            state = state.copy(
                isEmailValid = userDataValidator.isValidEmail(it.toString())
            )
        }.launchIn(viewModelScope)

        state.password.textAsFlow().onEach {
            state = state.copy(
                passwordValidationState = userDataValidator.validatePassword(it.toString())
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