package com.rfdotech.wear.run.domain

import com.rfdotech.core.connectivity.domain.DeviceNode
import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.connectivity.domain.messaging.MessagingError
import com.rfdotech.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PhoneConnector {
    val connectedDevice: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessagingAction>

    suspend fun sendActionToPhone(action: MessagingAction): EmptyResult<MessagingError>
}