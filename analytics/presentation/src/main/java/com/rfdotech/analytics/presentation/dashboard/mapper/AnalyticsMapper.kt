package com.rfdotech.analytics.presentation.dashboard.mapper

import android.content.Context
import com.rfdotech.analytics.domain.AnalyticsValues
import com.rfdotech.analytics.presentation.R
import com.rfdotech.analytics.presentation.dashboard.AnalyticsDashboardState
import com.rfdotech.analytics.presentation.dashboard.model.AnalyticsCardDataUiObject
import com.rfdotech.analytics.presentation.dashboard.model.AnalyticsDataUi
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import com.rfdotech.core.presentation.ui.appendTextToDayHourMinute
import com.rfdotech.core.presentation.ui.appendTextToHourMinuteSecond
import com.rfdotech.core.presentation.ui.appendTextToKilometer
import com.rfdotech.core.presentation.ui.appendTextToKilometerPerHour
import com.rfdotech.core.presentation.ui.formatted
import com.rfdotech.core.presentation.ui.toFormattedKm
import com.rfdotech.core.presentation.ui.toFormattedKmh
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

fun AnalyticsDashboardState.toAnalyticsCardDataUiObject(context: Context): AnalyticsCardDataUiObject {
    val totalDistanceRunStr = totalDistanceRun.toFormattedKm(context)
    val totalDistanceRunAcc = context.appendTextToKilometer(
        text = context.getString(R.string.total_distance_run),
        distanceKm = totalDistanceRun
    )

    val totalTimeRunStr = totalTimeRun.toFormattedTotalTime()
    val totalTimeRunAcc = context.appendTextToDayHourMinute(
        text = context.getString(R.string.total_time_run),
        time = totalTimeRun
    )

    val fastestEverRunStr = fastestEverRun.toFormattedKmh(context)
    val fastestEverRunAcc = context.appendTextToKilometerPerHour(
        text = context.getString(R.string.fastest_ever_run),
        distanceKm = fastestEverRun
    )

    val avgDistanceStr = avgDistance.toFormattedKm(context)
    val avgDistanceAcc = context.appendTextToKilometer(
        text = context.getString(R.string.acc_average_distance),
        distanceKm = avgDistance
    )

    val avgPaceStr = avgPace.formatted()
    val avgPaceAcc = context.appendTextToHourMinuteSecond(
        text = context.getString(R.string.acc_average_pace),
        time = avgPace
    )

    return AnalyticsCardDataUiObject(
        totalDistanceRun = AnalyticsDataUi(
            title = context.getString(R.string.total_distance_run),
            displayedValue = totalDistanceRunStr,
            contentDesc = totalDistanceRunAcc
        ), totalTimeRun = AnalyticsDataUi(
            title = context.getString(R.string.total_time_run),
            displayedValue = totalTimeRunStr,
            contentDesc = totalTimeRunAcc
        ), fastestEverRun = AnalyticsDataUi(
            title = context.getString(R.string.fastest_ever_run),
            displayedValue = fastestEverRunStr,
            contentDesc = fastestEverRunAcc
        ), avgDistance = AnalyticsDataUi(
            title = context.getString(R.string.average_distance),
            displayedValue = avgDistanceStr,
            contentDesc = avgDistanceAcc
        ), avgPace = AnalyticsDataUi(
            title = context.getString(R.string.average_pace),
            displayedValue = avgPaceStr,
            contentDesc = avgPaceAcc
        )
    )
}