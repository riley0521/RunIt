package com.rfdotech.core.connectivity.data.messaging

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Keep
@Serializable
sealed interface MessagingActionDto {

    @Keep
    @Serializable
    data object StartOrResumeRun: MessagingActionDto

    @Keep
    @Serializable
    data object Pause: MessagingActionDto

    @Keep
    @Serializable
    data object Finish: MessagingActionDto

    @Keep
    @Serializable
    data object Trackable: MessagingActionDto

    @Keep
    @Serializable
    data object NotTrackable: MessagingActionDto

    @Keep
    @Serializable
    data object ConnectionRequest: MessagingActionDto

    @Keep
    @Serializable
    data class HeartRateUpdate(val heartRate: Int): MessagingActionDto

    @Keep
    @Serializable
    data class DistanceUpdate(val distanceMeters: Int): MessagingActionDto

    @Keep
    @Serializable
    data class TimeUpdate(val elapsedTime: Duration): MessagingActionDto
}