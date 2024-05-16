package com.rfdotech.analytics.presentation

data class AnalyticsDashboardState(
    val totalDistanceRun: String = "0.0 km",
    val totalTimeRun: String = "0d 0h 0m",
    val fastestEverRun: String = "0.0 km/h",
    val avgDistance: String = "0.0 km",
    val avgPace: String = "00:00:00"
)
