package com.rfdotech.auth.presentation.intro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.auth.presentation.R
import com.rfdotech.core.presentation.designsystem.LogoIcon
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space12
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.Space48
import com.rfdotech.core.presentation.designsystem.Space8
import com.rfdotech.core.presentation.designsystem.components.GradientBackground
import com.rfdotech.core.presentation.designsystem.components.PrimaryButton
import com.rfdotech.core.presentation.designsystem.components.SecondaryButton
import com.rfdotech.core.presentation.designsystem.components.util.colorOnBackGround
import com.rfdotech.core.presentation.designsystem.components.util.colorOnSurfaceVariant

@Composable
fun IntroScreenRoot(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    IntroScreen(
        onAction = { action ->
            when (action) {
                IntroAction.OnSignInClick -> onSignInClick()
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
                text = stringResource(id = R.string.sign_in),
                isLoading = false,
                onClick = {
                    onAction(IntroAction.OnSignInClick)
                }
            )
            Spacer(modifier = Modifier.height(Space16))
            PrimaryButton(
                text = stringResource(id = R.string.sign_up),
                isLoading = false,
                onClick = {
                    onAction(IntroAction.OnSignUpClick)
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