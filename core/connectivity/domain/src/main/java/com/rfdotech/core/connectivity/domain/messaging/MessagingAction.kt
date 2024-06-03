package com.rfdotech.core.connectivity.domain.messaging

import kotlin.time.Duration

sealed interface MessagingAction {
    data object StartOrResumeRun: MessagingAction
    data object Pause: MessagingAction
    data object Finish: MessagingAction
    data object Trackable: MessagingAction
    data object NotTrackable: MessagingAction
    data object ConnectionRequest: MessagingAction
    data class HeartRateUpdate(val heartRate: Int): MessagingAction
    data class DistanceUpdate(val distanceMeters: Int): MessagingAction
    data class TimeUpdate(val elapsedTime: Duration): MessagingAction
}