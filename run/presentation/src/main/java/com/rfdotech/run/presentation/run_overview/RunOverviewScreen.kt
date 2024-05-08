@file:OptIn(ExperimentalMaterial3Api::class)

package com.rfdotech.run.presentation.run_overview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfdotech.core.presentation.designsystem.AnalyticsIcon
import com.rfdotech.core.presentation.designsystem.LogoIcon
import com.rfdotech.core.presentation.designsystem.RunIcon
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.SignOutIcon
import com.rfdotech.core.presentation.designsystem.components.MyFloatingActionButton
import com.rfdotech.core.presentation.designsystem.components.PrimaryScaffold
import com.rfdotech.core.presentation.designsystem.components.PrimaryToolbar
import com.rfdotech.core.presentation.designsystem.components.util.DropDownItem
import com.rfdotech.run.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onAnalyticsClick: () -> Unit,
    onStartClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(
        onAction = { action ->
            when (action) {
                RunOverviewAction.OnAnalyticsClick -> onAnalyticsClick()
                RunOverviewAction.OnStartClick -> onStartClick()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
private fun RunOverviewScreen(
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    val menuItems = listOf(
        DropDownItem(icon = AnalyticsIcon, title = stringResource(id = R.string.analytics)),
        DropDownItem(icon = SignOutIcon, title = stringResource(id = R.string.sign_out))
    )

    PrimaryScaffold(
        topAppBar = {
            PrimaryToolbar(
                title = stringResource(id = R.string.app_name),
                menuItems = menuItems,
                onMenuItemClick = { index ->
                    when (index) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnSignOutClick)
                        else -> Unit
                    }
                },
                scrollBehavior = scrollBehavior,
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            MyFloatingActionButton(
                icon = RunIcon,
                onClick = {
                    onAction(RunOverviewAction.OnStartClick)
                },
                contentDescription = stringResource(id = R.string.start_your_run),
                iconSize = 25.dp
            )
        }
    ) { padding ->

    }
}

@Preview
@Composable
private fun RunOverviewScreenScreenPreview() {
    RunItTheme {
        RunOverviewScreen(
            onAction = {}
        )
    }
}

