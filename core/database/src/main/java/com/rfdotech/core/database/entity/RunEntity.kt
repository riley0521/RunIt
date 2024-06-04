package com.rfdotech.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bson.types.ObjectId
import java.time.ZonedDateTime

@Entity(tableName = "tbl_runs")
data class RunEntity(
    val durationMillis: Long,
    val distanceMeters: Int,
    val dateTimeUtc: ZonedDateTime,
    val latitude: Double,
    val longitude: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val numberOfSteps: Int,
    val avgHeartRate: Int,
    val mapPictureUrl: String?,
    @PrimaryKey(autoGenerate = false)
    val id: String = ObjectId().toHexString()
)
