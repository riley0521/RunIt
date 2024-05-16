package com.rfdotech.analytics.presentation

sealed interface AnalyticsDashboardAction {
    data object OnBackClick: AnalyticsDashboardAction
}