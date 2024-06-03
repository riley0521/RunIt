package com.rfdotech.core.connectivity.data.messaging

import com.rfdotech.core.connectivity.domain.messaging.MessagingAction

fun MessagingAction.toMessagingActionDto(): MessagingActionDto {
    return when (this) {
        MessagingAction.ConnectionRequest -> MessagingActionDto.ConnectionRequest
        is MessagingAction.DistanceUpdate -> MessagingActionDto.DistanceUpdate(this.distanceMeters)
        MessagingAction.Finish -> MessagingActionDto.Finish
        is MessagingAction.HeartRateUpdate -> MessagingActionDto.HeartRateUpdate(this.heartRate)
        MessagingAction.NotTrackable -> MessagingActionDto.NotTrackable
        MessagingAction.Pause -> MessagingActionDto.Pause
        MessagingAction.StartOrResumeRun -> MessagingActionDto.StartOrResumeRun
        is MessagingAction.TimeUpdate -> MessagingActionDto.TimeUpdate(this.elapsedTime)
        MessagingAction.Trackable -> MessagingActionDto.Trackable
    }
}

fun MessagingActionDto.toMessagingAction(): MessagingAction {
    return when (this) {
        MessagingActionDto.ConnectionRequest -> MessagingAction.ConnectionRequest
        is MessagingActionDto.DistanceUpdate -> MessagingAction.DistanceUpdate(this.distanceMeters)
        MessagingActionDto.Finish -> MessagingAction.Finish
        is MessagingActionDto.HeartRateUpdate -> MessagingAction.HeartRateUpdate(this.heartRate)
        MessagingActionDto.NotTrackable -> MessagingAction.NotTrackable
        MessagingActionDto.Pause -> MessagingAction.Pause
        MessagingActionDto.StartOrResumeRun -> MessagingAction.StartOrResumeRun
        is MessagingActionDto.TimeUpdate -> MessagingAction.TimeUpdate(this.elapsedTime)
        MessagingActionDto.Trackable -> MessagingAction.Trackable
    }
}