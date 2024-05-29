package com.rfdotech.analytics.presentation.dashboard.model

data class AnalyticsCardDataUiObject(
    val totalDistanceRun: AnalyticsDataUi,
    val totalTimeRun: AnalyticsDataUi,
    val fastestEverRun: AnalyticsDataUi,
    val avgDistance: AnalyticsDataUi,
    val avgPace: AnalyticsDataUi
)

data class AnalyticsDataUi(
    val title: String,
    val displayedValue: String,
    val contentDesc: String = "$title . $displayedValue"
)