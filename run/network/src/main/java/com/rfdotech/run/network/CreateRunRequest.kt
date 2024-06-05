package com.rfdotech.run.network

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CreateRunRequest(
    val durationMillis: Long,
    val distanceMeters: Int,
    val epochMillis: Long,
    val lat: Double,
    val long: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val avgHeartRate: Int?,
    val maxHeartRate: Int?,
    val totalElevationMeters: Int,
    val id: String
)
