package com.rfdotech.auth.presentation.registration

sealed interface RegistrationAction {
    data object OnTogglePasswordVisibilityClick : RegistrationAction
    data object OnSignInClick: RegistrationAction
    data object OnSignUpClick: RegistrationAction
}