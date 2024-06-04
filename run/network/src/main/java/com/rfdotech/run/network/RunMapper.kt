package com.rfdotech.run.network

import com.rfdotech.core.domain.ZonedDateTimeHelper
import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.run.Run
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZonedDateTimeHelper.UTC_ZONE),
        distanceMeters = distanceMeters,
        location = Location(latitude = lat, longitude = long),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        numberOfSteps = 0,
        avgHeartRate = avgHeartRate ?: 0,
        mapPictureUrl = mapPictureUrl
    )
}

fun FirestoreRunDto.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.ofEpochMilli(dateTimeUtc).atZone(ZonedDateTimeHelper.UTC_ZONE),
        distanceMeters = distanceMeters,
        location = Location(latitude = latitude, longitude = longitude),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        numberOfSteps = numberOfSteps,
        avgHeartRate = avgHeartRate,
        mapPictureUrl = mapPictureUrl
    )
}

fun Run.toRunDtoV2(userId: String): FirestoreRunDto {
    return FirestoreRunDto(
        id = id.orEmpty(),
        userId = userId,
        dateTimeUtc = dateTimeUtc.toInstant().toEpochMilli(),
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        latitude = location.latitude,
        longitude = location.longitude,
        avgSpeedKmh = avgSpeedKhm,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        numberOfSteps = numberOfSteps,
        avgHeartRate = avgHeartRate,
        mapPictureUrl = mapPictureUrl
    )
}

fun Run.toCreateRunRequest(): CreateRunRequest {
    return CreateRunRequest(
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        epochMillis = dateTimeUtc.toEpochSecond() * 1000L,
        lat = location.latitude,
        long = location.longitude,
        avgSpeedKmh = avgSpeedKhm,
        maxSpeedKmh = maxSpeedKmh,
        avgHeartRate = avgHeartRate,
        maxHeartRate = 0, // We only need average heart rate
        totalElevationMeters = totalElevationMeters,
        id = id ?: throw IllegalArgumentException("Id not found.")
    )
}