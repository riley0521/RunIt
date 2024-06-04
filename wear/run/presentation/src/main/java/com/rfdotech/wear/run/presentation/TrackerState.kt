package com.rfdotech.wear.run.presentation

import android.content.Context
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import com.rfdotech.core.presentation.ui.toFormattedKm
import kotlin.time.Duration

data class TrackerState(
    val elapsedTime: Duration = Duration.ZERO,
    val distanceMeters: Int = 0,
    val heartRate: Int = 0,
    val isTrackable: Boolean = false,
    val hasStartedRunning: Boolean = false,
    val isConnectedPhoneNearby: Boolean = false,
    val isRunActive: Boolean = false,
    val canTrackHeartRate: Boolean = false,
    val isAmbientMode: Boolean = false,
    val burnInProtectionRequired: Boolean = false
) {
    fun getDistanceKmText(context: Context): String {
        return DistanceAndSpeedCalculator.getKmFromMeters(distanceMeters).toFormattedKm(context)
    }
}
