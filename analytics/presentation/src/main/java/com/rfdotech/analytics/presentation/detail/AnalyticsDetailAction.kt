package com.rfdotech.analytics.presentation.detail

import com.rfdotech.analytics.domain.DateParam

sealed interface AnalyticsDetailAction {
    data object OnBackClick: AnalyticsDetailAction
    data object OnToggleDatePickerDialog: AnalyticsDetailAction
    data class OnDateSelected(val startDate: DateParam, val endDate: DateParam): AnalyticsDetailAction
}