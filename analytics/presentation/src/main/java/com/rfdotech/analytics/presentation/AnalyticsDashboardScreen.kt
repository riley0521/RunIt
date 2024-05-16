@file:OptIn(ExperimentalMaterial3Api::class)

package com.rfdotech.analytics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.analytics.presentation.components.AnalyticsCard
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.components.PrimaryScaffold
import com.rfdotech.core.presentation.designsystem.components.PrimaryToolbar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsDashboardScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AnalyticsDashboardViewModel = koinViewModel()
) {
    AnalyticsDashboardScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                AnalyticsDashboardAction.OnBackClick -> {
                    onBackClick()
                }
            }
        }
    )
}

@Composable
private fun AnalyticsDashboardScreen(
    state: AnalyticsDashboardState,
    onAction: (AnalyticsDashboardAction) -> Unit
) {
    PrimaryScaffold(
        topAppBar = {
            PrimaryToolbar(
                title = stringResource(id = R.string.analytics),
                showBackButton = true,
                onBackClick = {
                    onAction(AnalyticsDashboardAction.OnBackClick)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(Space16)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space16)
            ) {
                AnalyticsCard(
                    title = stringResource(id = R.string.total_distance_run),
                    value = state.totalDistanceRun,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(Space16))
                AnalyticsCard(
                    title = stringResource(id = R.string.total_time_run),
                    value = state.totalTimeRun,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space16)
            ) {
                AnalyticsCard(
                    title = stringResource(id = R.string.fastest_ever_run),
                    value = state.fastestEverRun,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(Space16))
                AnalyticsCard(
                    title = stringResource(id = R.string.average_distance),
                    value = state.avgDistance,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space16)
            ) {
                AnalyticsCard(
                    title = stringResource(id = R.string.average_pace),
                    value = state.avgPace,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AnalyticsScreenPreview() {
    RunItTheme {
        AnalyticsDashboardScreen(
            state = AnalyticsDashboardState(
                totalDistanceRun = "173.8 km",
                totalTimeRun = "1d 2h 3m",
                fastestEverRun = "16.7 km/h",
                avgDistance = "17 km",
                avgPace = "00:00:00"
            ),
            onAction = {}
        )
    }
}
