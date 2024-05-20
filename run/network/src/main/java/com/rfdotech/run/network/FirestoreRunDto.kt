package com.rfdotech.run.network

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreRunDto(
    val id: String,
    val userId: String,
    val dateTimeUtc: String,
    val durationMillis: Long,
    val distanceMeters: Int,
    val latitude: Double,
    val longitude: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?
)
