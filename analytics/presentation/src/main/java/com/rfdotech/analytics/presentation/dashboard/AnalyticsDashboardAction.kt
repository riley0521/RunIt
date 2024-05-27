package com.rfdotech.analytics.presentation.dashboard

import com.rfdotech.analytics.domain.AnalyticDetailType

sealed interface AnalyticsDashboardAction {
    data object OnBackClick: AnalyticsDashboardAction
    data class OnNavigateToDetail(val analyticDetailType: AnalyticDetailType):
        AnalyticsDashboardAction
}