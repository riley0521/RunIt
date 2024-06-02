package com.rfdotech.wear.run.data

import com.rfdotech.core.connectivity.domain.DeviceNode
import com.rfdotech.core.connectivity.domain.DeviceType
import com.rfdotech.core.connectivity.domain.NodeDiscovery
import com.rfdotech.wear.run.domain.PhoneConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class WatchToPhoneConnector(
    private val nodeDiscovery: NodeDiscovery,
    private val applicationScope: CoroutineScope
): PhoneConnector {

    private val _connectedDevice = MutableStateFlow<DeviceNode?>(null)
    override val connectedDevice = _connectedDevice.asStateFlow()

    val messagingActions = nodeDiscovery
        .observeConnectedDevices(DeviceType.WATCH)
        .onEach { nodes ->
            val node = nodes.firstOrNull()
            if (node != null && node.isNearby) {
                _connectedDevice.update { node }
            }
        }
        .launchIn(applicationScope)
}