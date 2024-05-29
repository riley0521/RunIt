package com.rfdotech.core.presentation.ui

import android.content.Context
import java.util.Locale
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit

const val ONE_DECIMAL = 1
const val SECONDS_PER_MINUTE = 60

fun Duration.formatted(): String {
    val hours = this.getInt(DurationUnit.HOURS).formatNumberWithLeadingZero()
    val minutes = (this.getInt(DurationUnit.MINUTES) % SECONDS_PER_MINUTE).formatNumberWithLeadingZero()
    val seconds = (this.getInt(DurationUnit.SECONDS) % SECONDS_PER_MINUTE).formatNumberWithLeadingZero()

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

    val secondsPerKm = (this.inWholeSeconds / distanceKm).roundToInt()
    val avgPaceMinutes = secondsPerKm / SECONDS_PER_MINUTE
    val avgPaceSeconds = (secondsPerKm % SECONDS_PER_MINUTE).formatNumberWithLeadingZero()

    return context.getString(R.string.x_pace, "$avgPaceMinutes:$avgPaceSeconds")
}

fun Double.roundToDecimals(decimalCount: Int = ONE_DECIMAL): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}