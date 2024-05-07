@file:OptIn(ExperimentalFoundationApi::class)

package com.rfdotech.auth.presentation.sign_in

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState

data class SignInState(
    val email: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val isSigningIn: Boolean = false,
    val canSignIn: Boolean = false
)
