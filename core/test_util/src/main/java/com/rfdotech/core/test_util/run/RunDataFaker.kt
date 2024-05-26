package com.rfdotech.core.test_util.run

import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.run.Run
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun run(
    id: String? = null,
    duration: Duration = 30.minutes,
    dateTimeUtc: ZonedDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")),
    distanceMeters: Int = 2500,
    maxSpeedKmh: Double = 15.0
): Run {
    return Run(
        id = id,
        duration = duration,
        dateTimeUtc = dateTimeUtc,
        distanceMeters = distanceMeters,
        location = Location(latitude = 1.0, longitude = 1.0),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = 1,
        numberOfSteps = 8500,
        mapPictureUrl = null
    )
}