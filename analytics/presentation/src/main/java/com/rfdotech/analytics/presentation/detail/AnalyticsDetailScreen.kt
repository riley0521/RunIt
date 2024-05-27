@file:OptIn(ExperimentalMaterial3Api::class)

package com.rfdotech.analytics.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.analytics.domain.AnalyticDetailType
import com.rfdotech.analytics.domain.DateHelper
import com.rfdotech.analytics.presentation.AnalyticsSharedViewModel
import com.rfdotech.analytics.presentation.R
import com.rfdotech.analytics.presentation.dashboard.components.AnalyticsCardWithChart
import com.rfdotech.analytics.presentation.dashboard.components.getRunsWithDistance
import com.rfdotech.analytics.presentation.dashboard.model.AnalyticType
import com.rfdotech.core.presentation.designsystem.CalendarIcon
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.components.PrimaryScaffold
import com.rfdotech.core.presentation.designsystem.components.PrimaryToolbar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsDetailScreenRoot(
    analyticDetailType: AnalyticDetailType,
    onBackClick: () -> Unit,
    viewModel: AnalyticsSharedViewModel = koinViewModel()
) {
    AnalyticsDetailScreen(
        analyticDetailType = analyticDetailType,
        state = viewModel.detailState,
        onAction = { action ->
            when (action) {
                AnalyticsDetailAction.OnBackClick -> {
                    onBackClick()
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
private fun AnalyticsDetailScreen(
    analyticDetailType: AnalyticDetailType,
    state: AnalyticsDetailState,
    onAction: (AnalyticsDetailAction) -> Unit
) {
    val title = when (analyticDetailType) {
        AnalyticDetailType.DISTANCE -> stringResource(id = R.string.avg_distance_overtime)
        AnalyticDetailType.PACE -> stringResource(id = R.string.avg_pace_overtime)
    }

    val analyticType = remember(state.runs) {
        when (analyticDetailType) {
            AnalyticDetailType.DISTANCE -> AnalyticType.Distance(state.runs)
            AnalyticDetailType.PACE -> AnalyticType.Pace(state.runs)
        }
    }

    PrimaryScaffold(
        topAppBar = {
            PrimaryToolbar(
                title = title,
                showBackButton = true,
                onBackClick = {
                    onAction(AnalyticsDetailAction.OnBackClick)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Space16)
                .padding(bottom = Space32)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Space16)
        ) {
            ShowAndPickDateCard(
                title = DateHelper.getFormattedDate(state.startDate, state.endDate),
                onClick = {
                    onAction(AnalyticsDetailAction.OnToggleDatePickerDialog)
                },
                modifier = Modifier.fillMaxWidth()
            )
            AnalyticsCardWithChart(
                title = title,
                monthAndYear = "",
                analyticType = analyticType,
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth(),
                isDetailed = true
            )
        }
    }

    if (state.showDatePickerDialog) {
        // TODO: DatePicker Dialog
    }
}

@Composable
private fun ShowAndPickDateCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Space16))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Space16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title
        )
        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = CalendarIcon,
                contentDescription = stringResource(id = R.string.acc_pick_date)
            )
        }
    }
}

@Preview
@Composable
private fun AnalyticsDetailScreenPreview() {
    RunItTheme {
        AnalyticsDetailScreen(
            analyticDetailType = AnalyticDetailType.DISTANCE,
            state = AnalyticsDetailState(
                runs = getRunsWithDistance()
            ),
            onAction = {}
        )
    }
}
