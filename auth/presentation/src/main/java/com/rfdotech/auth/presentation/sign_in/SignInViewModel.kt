@file:OptIn(ExperimentalFoundationApi::class)

package com.rfdotech.auth.presentation.sign_in

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

class SignInViewModel(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator
) : ViewModel() {

    var state by mutableStateOf(SignInState())
        private set

    private val eventChannel = Channel<SignInEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        val emailFlow = state.email.textAsFlow()
        val passwordFlow = state.password.textAsFlow()

        combine(emailFlow, passwordFlow) { email, password ->
            val isValidEmail = userDataValidator.isValidEmail(email.toString())
            val canSignIn = isValidEmail && password.isNotEmpty()

            state = state.copy(canSignIn = canSignIn)
        }.launchIn(viewModelScope)
    }

    fun onAction(action: SignInAction) {
        when (action) {
            SignInAction.OnSignInClick -> signIn()
            SignInAction.OnTogglePasswordVisibility -> {
                state = state.copy(isPasswordVisible = !state.isPasswordVisible)
            }
            else -> Unit
        }
    }

    private fun signIn() = viewModelScope.launch {
        state = state.copy(isSigningIn = true)

        val email = state.email.text.toString().trim()
        val password = state.password.text.toString()
        val result = authRepository.signIn(
            email = email,
            password = password
        )

        state = state.copy(isSigningIn = false)

        when (result) {
            is Result.Error -> {
                with (result.error) {
                    if (this == DataError.Network.UNAUTHORIZED) {
                        eventChannel.send(SignInEvent.Error(UiText.StringResource(R.string.error_invalid_email_or_password)))
                    } else {
                        eventChannel.send(SignInEvent.Error(this.asUiText()))
                    }
                }
            }
            is Result.Success -> {
                eventChannel.send(SignInEvent.SignInSuccess)
            }
        }
    }
}