@file:OptIn(ExperimentalFoundationApi::class)

package com.rfdotech.auth.presentation.sign_up

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfdotech.auth.domain.PasswordValidationState
import com.rfdotech.auth.domain.UserDataValidator
import com.rfdotech.auth.presentation.R
import com.rfdotech.core.presentation.designsystem.CheckIcon
import com.rfdotech.core.presentation.designsystem.CrossIcon
import com.rfdotech.core.presentation.designsystem.EmailIcon
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.colorOnBackGround
import com.rfdotech.core.presentation.designsystem.colorOnSurfaceVariant
import com.rfdotech.core.presentation.designsystem.components.GradientBackground
import com.rfdotech.core.presentation.designsystem.components.PasswordTextField
import com.rfdotech.core.presentation.designsystem.components.PrimaryButton
import com.rfdotech.core.presentation.designsystem.components.PrimaryTextField
import com.rfdotech.core.presentation.designsystem.primaryFontFamily
import com.rfdotech.core.presentation.ui.showToastRes
import com.rfdotech.core.presentation.ui.showToastStr
import com.rfdotech.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreenRoot(
    onSignInClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is SignUpEvent.Error -> {
                keyboardController?.hide()
                context.showToastStr(event.error.asString(context))
            }
            SignUpEvent.SignUpSuccess -> {
                keyboardController?.hide()
                context.showToastRes(R.string.sign_up_successful)
                onSignUpSuccess()
            }
        }
    }

    SignUpScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                SignUpAction.OnSignInClick -> onSignInClick()
                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}

@Composable
private fun SignUpScreen(
    state: SignUpState,
    onAction: (SignUpAction) -> Unit
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
        ) {
            HeaderView(
                onSignInClick = {
                    onAction(SignUpAction.OnSignInClick)
                }
            )
            Spacer(modifier = Modifier.height(48.dp))
            EmailAndPasswordView(
                state = state,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(
                text = stringResource(id = R.string.sign_up),
                isLoading = state.isSigningUp,
                enabled = state.canSignUp && !state.isSigningUp,
                onClick = {
                    onAction(SignUpAction.OnSignUpClick)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HeaderView(
    onSignInClick: () -> Unit
) {
    Text(
        text = stringResource(id = R.string.create_account),
        style = MaterialTheme.typography.headlineMedium,
        color = colorOnBackGround
    )
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontFamily = primaryFontFamily,
                color = colorOnSurfaceVariant
            )
        ) {
            append(stringResource(id = R.string.already_have_an_account) + " ")
            pushStringAnnotation(
                tag = "btn_sign_in",
                annotation = stringResource(id = R.string.sign_in)
            )
            withStyle(
                style = SpanStyle(
                    fontFamily = primaryFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                append(stringResource(id = R.string.sign_in))
            }
        }
    }
    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "btn_sign_in",
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                onSignInClick()
            }
        }
    )
}

@Composable
private fun EmailAndPasswordView(
    state: SignUpState,
    onAction: (SignUpAction) -> Unit
) {
    val validEmailIcon = if (state.isEmailValid) {
        CheckIcon
    } else {
        null
    }

    PrimaryTextField(
        state = state.email,
        hint = stringResource(id = R.string.email_example),
        startIcon = EmailIcon,
        endIcon = validEmailIcon,
        title = stringResource(id = R.string.email),
        additionalInfo = stringResource(id = R.string.must_be_valid_email),
        keyboardType = KeyboardType.Email,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    PasswordTextField(
        state = state.password,
        hint = stringResource(id = R.string.password),
        title = stringResource(id = R.string.password),
        onTogglePasswordVisibility = {
            onAction(SignUpAction.OnTogglePasswordVisibilityClick)
        },
        isPasswordVisible = state.isPasswordVisible,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    PasswordRequirementListView(passwordValidationState = state.passwordValidationState)
}

@Composable
private fun PasswordRequirementListView(
    passwordValidationState: PasswordValidationState
) {
    PasswordRequirementView(
        text = stringResource(id = R.string.at_least_x_characters, UserDataValidator.MIN_PASSWORD_LENGTH),
        isValid = passwordValidationState.hasMinLength,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(4.dp))
    PasswordRequirementView(
        text = stringResource(id = R.string.at_least_one_number),
        isValid = passwordValidationState.hasNumber,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(4.dp))
    PasswordRequirementView(
        text = stringResource(id = R.string.contains_lowercase_character),
        isValid = passwordValidationState.hasLowerCaseCharacter,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(4.dp))
    PasswordRequirementView(
        text = stringResource(id = R.string.contains_uppercase_character),
        isValid = passwordValidationState.hasUpperCaseCharacter,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PasswordRequirementView(
    text: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    val isValidIcon = if (isValid) {
        CheckIcon
    } else {
        CrossIcon
    }

    val isValidIconColor = if (isValid) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = isValidIcon,
            contentDescription = null,
            tint = isValidIconColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun SignScreenPreview() {
    RunItTheme {
        SignUpScreen(
            state = SignUpState(
                passwordValidationState = PasswordValidationState(
                    hasNumber = true
                )
            ),
            onAction = {}
        )
    }
}