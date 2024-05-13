package com.rfdotech.core.presentation.ui

import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration

const val DECIMAL_COUNT = 1
const val SECONDS_PER_MINUTE = 60
const val SECONDS_PER_HOUR = 3600

fun Duration.formatted(): String {
    val totalSeconds = inWholeSeconds
    val hours = String.format("%02d", totalSeconds / SECONDS_PER_HOUR)
    val minutes = String.format("%02d", (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE)
    val seconds = String.format("%02d", totalSeconds % SECONDS_PER_MINUTE)

    return "$hours:$minutes:$seconds"
}

fun Double.toFormattedKm(): String {
    return "${this.roundToDecimals(DECIMAL_COUNT)} km"
}

fun Duration.toFormattedPace(distanceKm: Double): String {
    if (this == Duration.ZERO || distanceKm <= 0.0) {
        return "-"
    }

    val secondsPerKm = (this.inWholeSeconds / distanceKm).roundToInt()
    val avgPaceMinutes = secondsPerKm / DECIMAL_COUNT
    val avgPaceSeconds = String.format("%02d", secondsPerKm % SECONDS_PER_MINUTE)

    return "$avgPaceMinutes:$avgPaceSeconds / km"
}

private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}