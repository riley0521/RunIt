package com.rfdotech.core.data.run

import com.rfdotech.core.database.entity.DeletedRunSyncEntity
import com.rfdotech.core.database.entity.RunEntity
import com.rfdotech.core.database.entity.RunPendingSyncEntity
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun runPendingSyncEntity(
    id: String,
    userId: String,
    mapPictureBytes: ByteArray,
    duration: Duration = 30.minutes
): RunPendingSyncEntity {
    return RunPendingSyncEntity(
        run = RunEntity(
            durationMillis = duration.inWholeMilliseconds,
            distanceMeters = 2500,
            dateTimeUtc = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toInstant().toString(),
            latitude = 1.0,
            longitude = 1.0,
            avgSpeedKmh = 10.5,
            maxSpeedKmh = 15.0,
            totalElevationMeters = 1,
            mapPictureUrl = null,
            id = id
        ), runId = id, mapPictureBytes = mapPictureBytes, userId = userId
    )
}

fun deletedRunSyncEntity(
    runId: String,
    userId: String
): DeletedRunSyncEntity {
    return DeletedRunSyncEntity(
        runId = runId,
        userId = userId
    )
}