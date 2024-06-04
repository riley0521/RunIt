package com.rfdotech.core.database.mapper

import com.rfdotech.core.database.entity.RunEntity
import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.run.Run
import org.bson.types.ObjectId
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = dateTimeUtc,
        distanceMeters = distanceMeters,
        location = Location(latitude = latitude, longitude = longitude),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        numberOfSteps = numberOfSteps,
        avgHeartRate = avgHeartRate,
        mapPictureUrl = mapPictureUrl
    )
}

fun Run.toRunEntity(): RunEntity {
    return RunEntity(
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        dateTimeUtc = dateTimeUtc,
        latitude = location.latitude,
        longitude = location.longitude,
        avgSpeedKmh = avgSpeedKhm,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        numberOfSteps = numberOfSteps,
        avgHeartRate = avgHeartRate,
        mapPictureUrl = mapPictureUrl,
        id = id ?: ObjectId().toHexString()
    )
}