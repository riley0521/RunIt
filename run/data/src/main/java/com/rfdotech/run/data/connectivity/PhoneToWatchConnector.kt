@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rfdotech.run.data.connectivity

import com.rfdotech.core.connectivity.domain.DeviceNode
import com.rfdotech.core.connectivity.domain.DeviceType
import com.rfdotech.core.connectivity.domain.NodeDiscovery
import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.connectivity.domain.messaging.MessagingClient
import com.rfdotech.core.connectivity.domain.messaging.MessagingError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.run.domain.WatchConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

class PhoneToWatchConnector(
    private val nodeDiscovery: NodeDiscovery,
    private val applicationScope: CoroutineScope,
    private val messagingClient: MessagingClient
) : WatchConnector {

    private val _connectedDevice = MutableStateFlow<DeviceNode?>(null)
    override val connectedDevice: StateFlow<DeviceNode?> = _connectedDevice.asStateFlow()

    private val isTrackable = MutableStateFlow(false)

    override val messagingActions: Flow<MessagingAction> = nodeDiscovery
        .observeConnectedDevices(DeviceType.PHONE)
        .flatMapLatest { nodes ->
            // TODO: Create a chooser what smart watch to connect to because there might be another
            // watch nearby with the same capabilities and accidentally connect to it instead.
            val node = nodes.firstOrNull()
            if (node != null && node.isNearby) {
                _connectedDevice.update { node }
                messagingClient.connectToNode(node.id)
            } else flowOf()
        }.onEach { action ->
            if (action == MessagingAction.ConnectionRequest) {
                if (isTrackable.value) {
                    sendActionToWatch(MessagingAction.Trackable)
                } else {
                    sendActionToWatch(MessagingAction.NotTrackable)
                }
            }
        }.shareIn(applicationScope, SharingStarted.Eagerly)

    init {
        _connectedDevice
            .filterNotNull()
            .flatMapLatest { isTrackable }
            .onEach { isTrackable ->
                sendActionToWatch(MessagingAction.ConnectionRequest)

                val action = if (isTrackable) {
                    MessagingAction.Trackable
                } else {
                    MessagingAction.NotTrackable
                }
                sendActionToWatch(action)
            }
            .launchIn(applicationScope)
    }

    override suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError> {
        return messagingClient.sendOrQueueAction(action)
    }

    override fun setIsTrackable(isTrackable: Boolean) {
        this.isTrackable.value = isTrackable
    }
}