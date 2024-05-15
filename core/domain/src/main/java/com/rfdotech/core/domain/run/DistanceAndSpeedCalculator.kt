package com.rfdotech.core.domain.run

import com.rfdotech.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit

object DistanceAndSpeedCalculator  {

    private const val METERS_PER_KILOMETER = 1000.0

    fun getMaxSpeedKmh(locations: List<List<LocationTimestamp>>): Double {
        return locations.maxOf { locationSet ->
            locationSet.zipWithNext { locationTimestamp1, locationTimestamp2 ->
                val location1 = locationTimestamp1.location.location
                val location2 = locationTimestamp2.location.location

                val distanceMeters = location1.distanceTo(location2)
                val distanceKm = getKmFromMeters(distanceMeters.toInt())

                val hoursDiff = (locationTimestamp2.durationTimestamp - locationTimestamp1.durationTimestamp)
                    .toDouble(DurationUnit.HOURS)

                if (hoursDiff == 0.0) {
                    0.0
                } else {
                    (distanceKm) / hoursDiff
                }
            }.maxOrNull() ?: 0.0
        }
    }

    fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { locationSet ->
            locationSet.zipWithNext { locationTimestamp1, locationTimestamp2 ->
                val altitude1 = locationTimestamp1.location.altitude
                val altitude2 = locationTimestamp2.location.altitude
                (altitude2 - altitude1).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
    }

    fun getAvgSpeedKmh(distanceMeters: Int, duration: Duration): Double {
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
