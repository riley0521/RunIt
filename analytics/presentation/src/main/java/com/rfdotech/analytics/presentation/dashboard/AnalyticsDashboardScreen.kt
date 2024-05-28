@file:OptIn(ExperimentalMaterial3Api::class)

package com.rfdotech.analytics.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.analytics.domain.AnalyticDetailType
import com.rfdotech.analytics.domain.DateHelper
import com.rfdotech.analytics.presentation.AnalyticsSharedViewModel
import com.rfdotech.analytics.presentation.R
import com.rfdotech.analytics.presentation.dashboard.components.AnalyticsCard
import com.rfdotech.analytics.presentation.dashboard.components.AnalyticsCardWithChart
import com.rfdotech.analytics.presentation.dashboard.mapper.toFormattedTotalTime
import com.rfdotech.analytics.presentation.dashboard.model.AnalyticType
import com.rfdotech.core.domain.TextWithContentDesc
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.components.PrimaryScaffold
import com.rfdotech.core.presentation.designsystem.components.PrimaryToolbar
import com.rfdotech.core.presentation.ui.TDSAccessibilityManager
import com.rfdotech.core.presentation.ui.formatted
import com.rfdotech.core.presentation.ui.toFormattedKm
import com.rfdotech.core.presentation.ui.toFormattedKmh
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun AnalyticsDashboardScreenRoot(
    onBackClick: () -> Unit,
    onNavigateToDetail: (AnalyticDetailType) -> Unit,
    viewModel: AnalyticsSharedViewModel
) {
    AnalyticsDashboardScreen(
        state = viewModel.dashBoardState,
        onAction = { action ->
            when (action) {
                AnalyticsDashboardAction.OnBackClick -> {
                    onBackClick()
                }

                is AnalyticsDashboardAction.OnNavigateToDetail -> {
                    onNavigateToDetail(action.analyticDetailType)
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
    val tdsAccessibilityManager: TDSAccessibilityManager = koinInject()

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
                .padding(padding)
                .padding(bottom = Space32)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Space16)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space16)
            ) {
                val totalDistanceRunStr = state.totalDistanceRun.toFormattedKm()
                val totalDistanceRunAcc = tdsAccessibilityManager.appendTextToKilometer(
                    text = stringResource(id = R.string.total_distance_run),
                    distanceKm = state.totalDistanceRun
                )

                AnalyticsCard(
                    title = stringResource(id = R.string.total_distance_run),
                    value = totalDistanceRunStr,
                    contentDesc = totalDistanceRunAcc,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(Space16))

                val totalTimeRunStr = state.totalTimeRun.toFormattedTotalTime()
                val totalTimeRunAcc = tdsAccessibilityManager.appendTextToDayHourMinute(
                    text = stringResource(id = R.string.total_time_run),
                    time = state.totalTimeRun
                )

                AnalyticsCard(
                    title = stringResource(id = R.string.total_time_run),
                    value = totalTimeRunStr,
                    contentDesc = totalTimeRunAcc,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space16)
            ) {
                val fastestEverRunStr = state.fastestEverRun.toFormattedKmh()
                val fastestEverRunAcc = tdsAccessibilityManager.appendTextToKilometerPerHour(
                    text = stringResource(id = R.string.fastest_ever_run),
                    distanceKm = state.fastestEverRun
                )

                AnalyticsCard(
                    title = stringResource(id = R.string.fastest_ever_run),
                    value = fastestEverRunStr,
                    contentDesc = fastestEverRunAcc,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(Space16))

                val avgDistanceStr = state.avgDistance.toFormattedKm()
                val avgDistanceAcc = tdsAccessibilityManager.appendTextToKilometer(
                    text = stringResource(id = R.string.acc_average_distance),
                    distanceKm = state.avgDistance
                )

                AnalyticsCard(
                    title = stringResource(id = R.string.average_distance),
                    value = avgDistanceStr,
                    contentDesc = avgDistanceAcc,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space16)
            ) {
                val avgPaceStr = state.avgPace.formatted()
                val avgPaceAcc = tdsAccessibilityManager.appendTextToHourMinuteSecond(
                    text = stringResource(id = R.string.acc_average_pace),
                    time = state.avgPace
                )

                AnalyticsCard(
                    title = stringResource(id = R.string.average_pace),
                    value = avgPaceStr,
                    contentDesc = avgPaceAcc,
                    modifier = Modifier.weight(1f)
                )
            }
            AnalyticsCardWithChart(
                title = TextWithContentDesc(
                    text = stringResource(id = R.string.avg_distance_overtime),
                    contentDesc = stringResource(id = R.string.acc_avg_distance_overtime)
                ),
                monthAndYear = DateHelper.getMontAndYearFormatted(),
                analyticType = AnalyticType.Distance(state.runs),
                onClick = {
                    onAction(AnalyticsDashboardAction.OnNavigateToDetail(AnalyticDetailType.DISTANCE))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space16)
            )
            AnalyticsCardWithChart(
                title = TextWithContentDesc(
                    text = stringResource(id = R.string.avg_pace_overtime),
                    contentDesc = stringResource(id = R.string.acc_avg_pace_overtime)
                ),
                monthAndYear = DateHelper.getMontAndYearFormatted(),
                analyticType = AnalyticType.Pace(state.runs),
                onClick = {
                    onAction(AnalyticsDashboardAction.OnNavigateToDetail(AnalyticDetailType.PACE))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space16)
            )
        }
    }
}

@Preview
@Composable
private fun AnalyticsScreenPreview() {
    RunItTheme {
        AnalyticsDashboardScreen(
            state = AnalyticsDashboardState(
                totalDistanceRun = 173.8,
                totalTimeRun = 1.days.plus(2.hours).plus(3.minutes),
                fastestEverRun = 16.7,
                avgDistance = 17.0,
                avgPace = 69.seconds
            ),
            onAction = {}
        )
    }
}
