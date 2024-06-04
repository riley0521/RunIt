package com.rfdotech.run.presentation.run_overview.model

import com.rfdotech.core.domain.Address

data class RunUi(
    val id: String,
    val duration: String,
    val dateTime: String,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val pace: String,
    val totalElevation: String,
    val numberOfSteps: String,
    val avgHeartRate: String,
    val mapPictureUrl: String?,
    val address: Address?
)
