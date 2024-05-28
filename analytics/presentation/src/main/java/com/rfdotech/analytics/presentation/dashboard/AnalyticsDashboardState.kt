package com.rfdotech.analytics.presentation.dashboard

import com.rfdotech.core.domain.run.Run
import kotlin.time.Duration

data class AnalyticsDashboardState(
    val totalDistanceRun: Double = 0.0,
    val totalTimeRun: Duration = Duration.ZERO,
    val fastestEverRun: Double = 0.0,
    val avgDistance: Double = 0.0,
    val avgPace: Duration = Duration.ZERO,
    val runs: List<Run> = emptyList()
)
