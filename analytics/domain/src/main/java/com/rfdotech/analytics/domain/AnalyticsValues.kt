package com.rfdotech.analytics.domain

import kotlin.time.Duration

data class AnalyticsValues(
    val totalDistanceMeters: Int = 0,
    val totalTimeRun: Duration = Duration.ZERO,
    val fastestEverRun: Double = 0.0,
    val avgDistanceMeters: Double = 0.0,
    val avgPacePerRun: Double = 0.0
)
