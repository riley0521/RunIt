package com.rfdotech.core.presentation.ui

import android.content.Context
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import java.util.Locale
import kotlin.math.pow
import kotlin.math.round
import kotlin.time.Duration
import kotlin.time.DurationUnit

const val ONE_DECIMAL = 1
const val HOURS_PER_DAY = 24
const val SECONDS_PER_MINUTE = 60

fun Duration.formatted(): String {
    val hours = this.getInt(DurationUnit.HOURS).formatNumberWithLeadingZero()
    val minutes = this.getRemainingMinutes().formatNumberWithLeadingZero()
    val seconds = this.getRemainingSeconds().formatNumberWithLeadingZero()

    return "$hours:$minutes:$seconds"
}

fun Int.formatNumberWithLeadingZero(): String {
    return String.format(Locale.getDefault(), "%02d", this)
}

fun Double.toFormattedKm(context: Context): String {
    return context.getString(R.string.x_km, this.roundToDecimals().toString())
}

fun Double.toFormattedKmh(context: Context): String {
    return context.getString(R.string.x_km_per_hour, this.roundToDecimals().toString())
}

fun Int.toFormattedMeters(context: Context): String {
    return context.getString(R.string.x_meter, this)
}

fun Duration.toFormattedPace(distanceKm: Double, context: Context): String {
    if (this == Duration.ZERO || distanceKm <= 0.0) {
        return "-"
    }

    val averagePace = DistanceAndSpeedCalculator.getAveragePacePerKilometer(
        distanceKm,
        this
    ) ?: return ""

    return context.getString(
        R.string.x_pace,
        "${averagePace.minutes}:${averagePace.seconds.formatNumberWithLeadingZero()}"
    )
}

fun Double.roundToDecimals(decimalCount: Int = ONE_DECIMAL): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}