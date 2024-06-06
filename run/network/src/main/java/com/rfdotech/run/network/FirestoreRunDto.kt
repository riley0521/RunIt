package com.rfdotech.run.network

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class FirestoreRunDto(
    val id: String = "",
    val userId: String = "",
    val dateTimeUtc: Long = 0,
    val createdAt: Timestamp = Timestamp.now(), // Timestamp when it was pushed to server.
    val durationMillis: Long = 0,
    val distanceMeters: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val avgSpeedKmh: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val totalElevationMeters: Int = 0,
    val numberOfSteps: Int = 0,
    val avgHeartRate: Int = 0,
    val mapPictureUrl: String? = null
)
