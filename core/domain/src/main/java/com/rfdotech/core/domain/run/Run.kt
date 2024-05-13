package com.rfdotech.core.domain.run

import com.rfdotech.core.domain.location.Location
import java.time.ZonedDateTime
import kotlin.time.Duration

data class Run(
    val id: String?, // null if new run
    val duration: Duration,
    val dateTimeUtc: ZonedDateTime,
    val distanceMeters: Int,
    val location: Location,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?
) {
    val avgSpeedKhm: Double
        get() = DistanceAndSpeedCalculator.getAvgSpeedKhm(distanceMeters, duration)
}
