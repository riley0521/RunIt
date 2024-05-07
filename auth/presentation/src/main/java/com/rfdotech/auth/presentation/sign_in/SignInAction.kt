package com.rfdotech.auth.presentation.sign_in

sealed interface SignInAction {
    data object OnTogglePasswordVisibility : SignInAction
    data object OnSignInClick: SignInAction
    data object OnSignUpClick: SignInAction
}