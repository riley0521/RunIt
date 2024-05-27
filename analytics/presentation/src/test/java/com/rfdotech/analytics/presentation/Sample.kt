package com.rfdotech.analytics.presentation

import com.rfdotech.analytics.domain.AnalyticsRepository
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.round
import kotlin.math.roundToInt

class Sample(
    private val analyticsRepository: AnalyticsRepository
) {

    suspend fun foo() {
        val runs = analyticsRepository.getAllRunsThisMonth()
        val maxDistance = runs.maxOfOrNull { DistanceAndSpeedCalculator.getKmFromMeters(it.distanceMeters).roundToInt() } ?: return
        val minDistance = 0

        // For Y
        val distanceStep = (maxDistance - minDistance) / 5f
        val yData = (0..5).map { i ->
            (minDistance + distanceStep * i).roundToInt()
        }

        // The X Should be the current runs from this month
        val sortedRunsByDate = runs.sortedBy { it.dateTimeUtc }
    }

    fun formatDate(date: ZonedDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd")
        return formatter.format(date)
    }
}