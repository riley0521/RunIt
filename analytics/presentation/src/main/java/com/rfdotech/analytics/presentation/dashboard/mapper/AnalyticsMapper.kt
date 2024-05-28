package com.rfdotech.analytics.presentation.dashboard.mapper

import com.rfdotech.analytics.domain.AnalyticsValues
import com.rfdotech.analytics.presentation.dashboard.AnalyticsDashboardState
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

fun Duration.toFormattedTotalTime(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60

    return "${days}d ${hours}h ${minutes}m"
}

fun AnalyticsValues.toAnalyticsDashboardState(): AnalyticsDashboardState {
    val totalDistanceRunKm = DistanceAndSpeedCalculator.getKmFromMeters(totalDistanceMeters)
    val avgDistanceKm = DistanceAndSpeedCalculator.getKmFromMeters(avgDistanceMeters)

    return AnalyticsDashboardState(
        totalDistanceRun = totalDistanceRunKm,
        totalTimeRun = totalTimeRun,
        fastestEverRun = fastestEverRun,
        avgDistance = avgDistanceKm,
        avgPace = avgPacePerRun.seconds
    )
}