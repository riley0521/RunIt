@file:OptIn(ExperimentalFoundationApi::class)

package com.rfdotech.auth.presentation.sign_up

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import com.rfdotech.auth.domain.PasswordValidationState

data class SignUpState(
    val email: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val passwordValidationState: PasswordValidationState = PasswordValidationState(),
    val isSigningUp: Boolean = false,
    val canSignUp: Boolean = false
)
