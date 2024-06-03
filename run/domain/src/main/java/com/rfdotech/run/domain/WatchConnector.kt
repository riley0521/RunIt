package com.rfdotech.run.domain

import com.rfdotech.core.connectivity.domain.DeviceNode
import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.connectivity.domain.messaging.MessagingError
import com.rfdotech.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WatchConnector {
    val connectedDevice: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessagingAction>

    suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError>
    fun setIsTrackable(isTrackable: Boolean)
}