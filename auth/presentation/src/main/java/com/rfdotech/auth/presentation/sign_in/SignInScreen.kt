@file:Suppress("OPT_IN_USAGE_FUTURE_ERROR")
@file:OptIn(ExperimentalFoundationApi::class)

package com.rfdotech.auth.presentation.sign_in

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
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
import com.rfdotech.auth.presentation.R
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
fun SignInScreenRoot(
    onSignUpClick: () -> Unit,
    onSignInSuccess: () -> Unit,
    viewModel: SignInViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is SignInEvent.Error -> {
                keyboardController?.hide()
                context.showToastStr(event.error.asString(context))
            }
            SignInEvent.SignInSuccess -> {
                keyboardController?.hide()
                context.showToastRes(R.string.sign_in_successful)
                onSignInSuccess()
            }
        }
    }

    SignInScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                SignInAction.OnSignUpClick -> onSignUpClick()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
private fun SignInScreen(
    state: SignInState,
    onAction: (SignInAction) -> Unit
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(vertical = 32.dp)
                .padding(top = 16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.hi_there),
                style = MaterialTheme.typography.headlineMedium,
                color = colorOnBackGround
            )
            Text(
                text = stringResource(id = R.string.welcome_description_alt),
                style = MaterialTheme.typography.bodySmall,
                color = colorOnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(48.dp))

            PrimaryTextField(
                state = state.email,
                hint = stringResource(id = R.string.email_example),
                title = stringResource(id = R.string.email),
                startIcon = EmailIcon,
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(
                state = state.password,
                hint = stringResource(id = R.string.password),
                title = stringResource(id = R.string.password),
                onTogglePasswordVisibility = {
                    onAction(SignInAction.OnTogglePasswordVisibility)
                },
                isPasswordVisible = state.isPasswordVisible,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(
                text = stringResource(id = R.string.sign_in),
                isLoading = state.isSigningIn,
                enabled = state.canSignIn && !state.isSigningIn,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAction(SignInAction.OnSignInClick)
                }
            )
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontFamily = primaryFontFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    append(stringResource(id = R.string.dont_have_an_account) + " ")
                    pushStringAnnotation(
                        tag = "btn_sign_up",
                        annotation = stringResource(id = R.string.sign_up)
                    )
                    withStyle(
                        style = SpanStyle(
                            fontFamily = primaryFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(stringResource(id = R.string.sign_up))
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ClickableText(
                    text = annotatedString,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(
                            tag = "btn_sign_up",
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            onAction(SignInAction.OnSignUpClick)
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SignInScreenPreview() {
    RunItTheme {
        SignInScreen(
            state = SignInState(),
            onAction = {}
        )
    }
}
