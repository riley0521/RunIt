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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(SignInState())
        private set

    private val eventChannel = Channel<SignInEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        val email = state.email.textAsFlow()
        val password = state.password.textAsFlow()

        combine(email, password) { emailStr, passwordStr ->
            val canSignIn = emailStr.isNotBlank() && passwordStr.isNotBlank() && !state.isSigningIn

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

        // TODO: Add signIn function to AuthRepository

        state = state.copy(isSigningIn = false)
    }
}