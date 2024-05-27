package com.rfdotech.analytics.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.analytics.domain.AnalyticsRepository
import com.rfdotech.analytics.domain.DateParam
import com.rfdotech.analytics.domain.toZonedDateTime
import com.rfdotech.analytics.presentation.dashboard.AnalyticsDashboardState
import com.rfdotech.analytics.presentation.dashboard.mapper.toAnalyticsDashboardState
import com.rfdotech.analytics.presentation.detail.AnalyticsDetailAction
import com.rfdotech.analytics.presentation.detail.AnalyticsDetailState
import kotlinx.coroutines.launch

class AnalyticsSharedViewModel(
    private val analyticsRepository: AnalyticsRepository
): ViewModel() {

    var dashBoardState by mutableStateOf(AnalyticsDashboardState())
        private set

    var detailState by mutableStateOf(AnalyticsDetailState())
        private set

    init {
        viewModelScope.launch {
            val analyticsValues = analyticsRepository.getAnalyticsValues()
            dashBoardState = analyticsValues.toAnalyticsDashboardState()

            val allRunsThisMonth = analyticsRepository.getAllRunsThisMonth()
            dashBoardState = dashBoardState.copy(runs = allRunsThisMonth)
        }
    }

    fun getInitialRuns() {
        val startDate = detailState.startDate.let {
            DateParam(year = it.year, month = it.monthValue, day = it.dayOfMonth)
        }
        val endDate = detailState.endDate.let {
            DateParam(year = it.year, month = it.monthValue, day = it.dayOfMonth)
        }

        getRunsBetweenDates(startDate, endDate)
    }

    fun onAction(action: AnalyticsDetailAction) {
        when (action) {
            is AnalyticsDetailAction.OnDateSelected -> getRunsBetweenDates(action.startDate, action.endDate)
            AnalyticsDetailAction.OnToggleDatePickerDialog -> {
                detailState = detailState.copy(showDatePickerDialog = !detailState.showDatePickerDialog)
            }
            else -> Unit
        }
    }

    private fun getRunsBetweenDates(startDate: DateParam, endDate: DateParam) = viewModelScope.launch {
        detailState = detailState.copy(showDatePickerDialog = false)

        val startDateZoned = startDate.toZonedDateTime()
        val endDateZoned = endDate.toZonedDateTime(isEnd = true)

        if (startDateZoned > endDateZoned) {
            return@launch
        }

        detailState = detailState.copy(isGettingRuns = true)

        val runs = analyticsRepository.getAllRunsBetweenDates(startDate, endDate)
        detailState = detailState.copy(runs = runs)

        detailState = detailState.copy(isGettingRuns = false)
    }
}