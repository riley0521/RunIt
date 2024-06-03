@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rfdotech.wear.run.data

import com.rfdotech.core.connectivity.domain.DeviceNode
import com.rfdotech.core.connectivity.domain.DeviceType
import com.rfdotech.core.connectivity.domain.NodeDiscovery
import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.connectivity.domain.messaging.MessagingClient
import com.rfdotech.core.connectivity.domain.messaging.MessagingError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.wear.run.domain.PhoneConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

class WatchToPhoneConnector(
    private val nodeDiscovery: NodeDiscovery,
    private val applicationScope: CoroutineScope,
    private val messagingClient: MessagingClient
): PhoneConnector {

    private val _connectedDevice = MutableStateFlow<DeviceNode?>(null)
    override val connectedDevice: StateFlow<DeviceNode?> = _connectedDevice.asStateFlow()

    override val messagingActions: Flow<MessagingAction> = nodeDiscovery
        .observeConnectedDevices(DeviceType.WATCH)
        .flatMapLatest { nodes ->
            val node = nodes.firstOrNull()
            if (node != null && node.isNearby) {
                _connectedDevice.update { node }
                messagingClient.connectToNode(node.id)
            } else flowOf()
        }.shareIn(applicationScope, SharingStarted.Eagerly)

    override suspend fun sendActionToPhone(action: MessagingAction): EmptyResult<MessagingError> {
        return messagingClient.sendOrQueueAction(action)
    }
}