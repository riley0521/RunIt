@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.rfdotech.analytics.presentation.detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.rfdotech.analytics.domain.AnalyticDetailType
import com.rfdotech.analytics.presentation.DateHelper
import com.rfdotech.analytics.presentation.AnalyticsSharedViewModel
import com.rfdotech.analytics.presentation.R
import com.rfdotech.analytics.presentation.dashboard.components.AnalyticsCardWithChart
import com.rfdotech.analytics.presentation.dashboard.components.getRunsWithPace
import com.rfdotech.analytics.presentation.dashboard.model.AnalyticType
import com.rfdotech.core.domain.TextWithContentDesc
import com.rfdotech.core.presentation.designsystem.CalendarIcon
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.components.PrimaryScaffold
import com.rfdotech.core.presentation.designsystem.components.PrimaryToolbar
import com.rfdotech.core.presentation.ui.getTextForDateRange
import java.time.LocalDate

@Composable
fun AnalyticsDetailScreenRoot(
    analyticDetailType: AnalyticDetailType,
    onBackClick: () -> Unit,
    viewModel: AnalyticsSharedViewModel
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
    val context = LocalContext.current

    val title = when (analyticDetailType) {
        AnalyticDetailType.DISTANCE -> TextWithContentDesc(
            text = stringResource(id = R.string.avg_distance_overtime),
            contentDesc = stringResource(id = R.string.acc_avg_distance_overtime)
        )
        AnalyticDetailType.PACE -> TextWithContentDesc(
            text = stringResource(id = R.string.avg_pace_overtime),
            contentDesc = stringResource(id = R.string.acc_avg_pace_overtime)
        )
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
                title = title.text,
                titleContentDesc = title.contentDesc,
                showBackButton = true,
                onBackClick = {
                    onAction(AnalyticsDetailAction.OnBackClick)
                }
            )
        }
    ) { padding ->
        if (state.isGettingRuns) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Space16)
                    .padding(bottom = Space32)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Space16)
            ) {
                ShowAndPickDateRangeCard(
                    startDate = state.startDate.toLocalDate(),
                    endDate = state.endDate.toLocalDate(),
                    onClick = {
                        onAction(AnalyticsDetailAction.OnToggleDatePickerDialog)
                    },
                    formatDates = {
                        context.getTextForDateRange(
                            startDate = state.startDate.toLocalDate(),
                            endDate = state.endDate.toLocalDate()
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                AnalyticsCardWithChart(
                    title = title,
                    monthAndYear = "",
                    analyticType = analyticType,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    isDetailed = true
                )
            }
        }
    }

    val calendarState = rememberUseCaseState(
        onDismissRequest = {
            onAction(AnalyticsDetailAction.OnToggleDatePickerDialog)
        },
        onFinishedRequest = {
            onAction(AnalyticsDetailAction.OnToggleDatePickerDialog)
        }
    )
    CalendarDialog(
        state = calendarState,
        selection = CalendarSelection.Period { startDate, endDate ->
            val selectedStartDate = DateHelper.convertLocalDateToDateParam(startDate)
            val selectedEndDate = DateHelper.convertLocalDateToDateParam(endDate)

            onAction(AnalyticsDetailAction.OnDateSelected(selectedStartDate, selectedEndDate))
        },
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH,
            boundary = DateHelper.getAllowedDates()
        )
    )

    LaunchedEffect(state.showDatePickerDialog) {
        if (state.showDatePickerDialog) {
            calendarState.show()
        }
    }
}

@Composable
private fun ShowAndPickDateRangeCard(
    startDate: LocalDate,
    endDate: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    formatDates: (() -> String)? = null
) {
    val title = DateHelper.getFormattedDate(startDate, endDate)
    val titleAcc = formatDates?.invoke() ?: title

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Space16))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Space16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier
                .semantics {
                    contentDescription = titleAcc
                }
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
        var state by remember {
            mutableStateOf(
                AnalyticsDetailState()
            )
        }

        AnalyticsDetailScreen(
            analyticDetailType = AnalyticDetailType.PACE,
            state = state,
            onAction = { action ->
                when (action) {
                    AnalyticsDetailAction.OnBackClick -> {
                        state = state.copy(runs = getRunsWithPace())
                        Log.d("GETTING_RUNS", "here")
                    }
                    is AnalyticsDetailAction.OnDateSelected -> {
                        Log.d("SELECTED_DATE", action.startDate.toString())
                        Log.d("SELECTED_DATE", action.endDate.toString())
                    }
                    AnalyticsDetailAction.OnToggleDatePickerDialog -> {
                        state = state.copy(showDatePickerDialog = !state.showDatePickerDialog)
                    }
                }
            }
        )
    }
}
