package com.rfdotech.run.network

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreRunDto(
    val id: String = "",
    val userId: String = "",
    val dateTimeUtc: String = "",
    val durationMillis: Long = 0,
    val distanceMeters: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val avgSpeedKmh: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val totalElevationMeters: Int = 0,
    val mapPictureUrl: String? = null
)
