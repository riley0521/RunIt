package com.rfdotech.core.database

import com.rfdotech.core.database.entity.RunEntity
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun runEntity(
    duration: Duration = 30.minutes,
    distanceMeters: Int = 0,
    dateTimeUtc: ZonedDateTime = ZonedDateTime.now(),
    maxSpeedKmh: Double = 15.0
): RunEntity {
    return RunEntity(
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        dateTimeUtc = dateTimeUtc,
        latitude = 14.624703,
        longitude = 121.120091,
        avgSpeedKmh = 10.5,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = 1,
        numberOfSteps = 8500,
        avgHeartRate = 124,
        mapPictureUrl = null
    )
}