@file:OptIn(ExperimentalFoundationApi::class)

package com.rfdotech.auth.presentation.sign_up

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.auth.domain.AuthRepository
import com.rfdotech.auth.domain.UserDataValidator
import com.rfdotech.auth.presentation.R
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.presentation.ui.UiText
import com.rfdotech.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator
): ViewModel() {

    var state by mutableStateOf(SignUpState())
        private set

    private val eventChannel = Channel<SignUpEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        val emailFlow = state.email.textAsFlow()
        val passwordFlow = state.password.textAsFlow()

        combine(emailFlow, passwordFlow) { email, password ->
            val isEmailValid = userDataValidator.isValidEmail(email.toString())
            val passwordValidationState = userDataValidator.validatePassword(password.toString())
            val canRegister = isEmailValid && passwordValidationState.isValidPassword && !state.isRegistering

            state = state.copy(
                isEmailValid = isEmailValid,
                passwordValidationState = passwordValidationState,
                canRegister = canRegister
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: SignUpAction) {
        when (action) {
            SignUpAction.OnSignUpClick -> signUp()
            SignUpAction.OnTogglePasswordVisibilityClick -> {
                state = state.copy(isPasswordVisible = !state.isPasswordVisible)
            }
            else -> Unit
        }
    }

    private fun signUp() = viewModelScope.launch {
        state = state.copy(isRegistering = true)

        val email = state.email.text.toString().trim()
        val password = state.password.text.toString()

        val result = authRepository.signUp(email, password)
        state = state.copy(isRegistering = false)

        when (result) {
            is Result.Error -> {
                if (result.error == DataError.Network.CONFLICT) {
                    eventChannel.send(SignUpEvent.Error(UiText.StringResource(R.string.error_email_exists, arrayOf(email))))
                } else {
                    eventChannel.send(SignUpEvent.Error(result.error.asUiText()))
                }
            }
            is Result.Success -> {
                eventChannel.send(SignUpEvent.SignUpSuccess)
            }
        }
    }
}