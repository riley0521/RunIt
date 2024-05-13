package com.rfdotech.core.domain.run

import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit

object DistanceAndSpeedCalculator  {

    private const val METERS_PER_KILOMETER = 1000.0

    fun getAvgSpeedKhm(distanceMeters: Int, duration: Duration): Double {
        return getKmFromMeters(distanceMeters) / duration.toDouble(DurationUnit.HOURS)
    }

    fun getKmFromMeters(distanceMeters: Int): Double {
        return (distanceMeters / METERS_PER_KILOMETER)
    }

    fun getAvgSecondsPerKm(distanceKm: Double, duration: Duration): Int {
        return if (distanceKm == 0.0) {
            0
        } else {
            (duration.inWholeSeconds / distanceKm).roundToInt()
        }
    }
}
