package com.rfdotech.core.data.run

import com.rfdotech.core.database.entity.DeletedRunSyncEntity
import com.rfdotech.core.database.entity.RunEntity
import com.rfdotech.core.database.entity.RunPendingSyncEntity
import com.rfdotech.core.domain.ZonedDateTimeHelper
import java.time.ZonedDateTime
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
            dateTimeUtc = ZonedDateTimeHelper.addZoneIdToZonedDateTime(ZonedDateTime.now()),
            latitude = 1.0,
            longitude = 1.0,
            avgSpeedKmh = 10.5,
            maxSpeedKmh = 15.0,
            totalElevationMeters = 1,
            numberOfSteps = 8500,
            avgHeartRate = 150,
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