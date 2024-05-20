package com.rfdotech.run.network

import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.run.Run
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(latitude = lat, longitude = long),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}

fun RunDtoV2.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(latitude = latitude, longitude = longitude),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}

fun Run.toRunDtoV2(userId: String): RunDtoV2 {
    return RunDtoV2(
        id = id.orEmpty(),
        userId = userId,
        dateTimeUtc = dateTimeUtc.toInstant().toString(),
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        latitude = location.latitude,
        longitude = location.longitude,
        avgSpeedKmh = avgSpeedKhm,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
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
        totalElevationMeters = totalElevationMeters,
        id = id ?: throw IllegalArgumentException("Id not found.")
    )
}