package com.rfdotech.auth.presentation.sign_up

sealed interface SignUpAction {
    data object OnTogglePasswordVisibilityClick : SignUpAction
    data object OnSignInClick: SignUpAction
    data object OnSignUpClick: SignUpAction
}