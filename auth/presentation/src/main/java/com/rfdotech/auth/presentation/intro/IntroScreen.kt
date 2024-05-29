package com.rfdotech.auth.presentation.intro

import android.app.Activity
import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.rfdotech.auth.presentation.R
import com.rfdotech.core.presentation.designsystem.LogoIcon
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space12
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.Space48
import com.rfdotech.core.presentation.designsystem.Space8
import com.rfdotech.core.presentation.designsystem.colorOnBackGround
import com.rfdotech.core.presentation.designsystem.colorOnSurfaceVariant
import com.rfdotech.core.presentation.designsystem.components.GradientBackground
import com.rfdotech.core.presentation.designsystem.components.SecondaryButton
import com.rfdotech.core.presentation.ui.ObserveAsEvents
import com.rfdotech.core.presentation.ui.auth.GoogleAuthUiClient
import com.rfdotech.core.presentation.ui.showToastRes
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun IntroScreenRoot(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignInSuccess: () -> Unit,
    googleAuthUiClient: GoogleAuthUiClient = koinInject(),
    viewModel: IntroViewModel = koinViewModel()
) {
    val activityContext = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember {
        mutableStateOf(false)
    }

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                coroutineScope.launch {
                    isLoading = true

                    val userId = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )

                    if (userId != null) {
                        viewModel.onSignInSuccess(userId)
                    }
                }
            }
        }
    )

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            IntroEvent.OnSignInSuccessful -> {
                isLoading = false
                onSignInSuccess()
            }
        }
    }

    if (isLoading) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    IntroScreen(
        onAction = { action ->
            when (action) {
                IntroAction.OnSignInClick -> {
                    coroutineScope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        if (signInIntentSender == null) {
                            activityContext.showToastRes(R.string.error_no_signed_in_user)
                            return@launch
                        }

                        authLauncher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender
                            ).build()
                        )
                    }
                }
                IntroAction.OnSignUpClick -> onSignUpClick()
            }
        }
    )
}

@Composable
private fun IntroScreen(
    onAction: (IntroAction) -> Unit
) {
    GradientBackground {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            LogoVertical()
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space16)
                .padding(bottom = Space48)
        ) {
            Text(
                text = stringResource(id = R.string.welcome_title),
                style = MaterialTheme.typography.headlineSmall,
                color = colorOnBackGround
            )
            Spacer(modifier = Modifier.height(Space8))
            Text(
                text = stringResource(id = R.string.welcome_description),
                style = MaterialTheme.typography.bodySmall,
                color = colorOnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Space32))
            SecondaryButton(
                text = stringResource(id = R.string.sign_in_with_google),
                isLoading = false,
                onClick = {
                    onAction(IntroAction.OnSignInClick)
                }
            )
        }
    }
}

@Composable
private fun LogoVertical(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = LogoIcon,
            contentDescription = stringResource(id = R.string.acc_logo),
            tint = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(Space12))
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            color = colorOnBackGround
        )
    }
}

@Preview
@Composable
private fun IntroScreenPreview() {
    RunItTheme {
        IntroScreen(
            onAction = {}
        )
    }
}