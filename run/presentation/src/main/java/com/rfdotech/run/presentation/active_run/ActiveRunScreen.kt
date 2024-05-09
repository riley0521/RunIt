@file:OptIn(ExperimentalMaterial3Api::class)

package com.rfdotech.run.presentation.active_run

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfdotech.core.presentation.designsystem.PauseIcon
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.StartIcon
import com.rfdotech.core.presentation.designsystem.StopIcon
import com.rfdotech.core.presentation.designsystem.components.MyFloatingActionButton
import com.rfdotech.core.presentation.designsystem.components.PrimaryScaffold
import com.rfdotech.core.presentation.designsystem.components.PrimaryToolbar
import com.rfdotech.run.domain.RunData
import com.rfdotech.run.presentation.R
import com.rfdotech.run.presentation.active_run.components.RunDataCard
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.minutes

@Composable
fun ActiveRunScreenRoot(
    viewModel: ActiveRunViewModel = koinViewModel()
) {
    ActiveRunScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onAction: (ActiveRunAction) -> Unit
) {

    PrimaryScaffold(
        withGradient = false,
        topAppBar = {
            PrimaryToolbar(
                showBackButton = true,
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                },
                title = stringResource(id = R.string.active_run)
            )
        },
        floatingActionButton = {
            val fabIcon = if (state.shouldTrack) {
                StopIcon
            } else {
                StartIcon
            }

            val fabContentDesc = if (state.shouldTrack) {
                stringResource(id = R.string.pause_run)
            } else {
                stringResource(id = R.string.start_run)
            }

            MyFloatingActionButton(
                icon = fabIcon,
                onClick = {
                    onAction(ActiveRunAction.OnToggleRunClick)
                },
                iconSize = 20.dp,
                contentDescription = fabContentDesc
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(padding)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunItTheme {
        ActiveRunScreen(
            state = ActiveRunState(
                elapsedTime = 10.minutes,
                runData = RunData(
                    distanceMeters = 800,
                    pace = 3.minutes
                )
            ),
            onAction = {}
        )
    }
}
