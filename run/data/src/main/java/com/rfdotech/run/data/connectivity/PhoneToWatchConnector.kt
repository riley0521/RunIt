package com.rfdotech.run.data.connectivity

import com.rfdotech.core.connectivity.domain.DeviceNode
import com.rfdotech.core.connectivity.domain.DeviceType
import com.rfdotech.core.connectivity.domain.NodeDiscovery
import com.rfdotech.run.domain.WatchConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class PhoneToWatchConnector(
    private val nodeDiscovery: NodeDiscovery,
    private val applicationScope: CoroutineScope
) : WatchConnector {

    private val _connectedDevice = MutableStateFlow<DeviceNode?>(null)
    override val connectedDevice = _connectedDevice.asStateFlow()

    private val isTrackable = MutableStateFlow(false)

    val messagingActions = nodeDiscovery
        .observeConnectedDevices(DeviceType.PHONE)
        .onEach { nodes ->
            // TODO: Create a chooser what smart watch to connect to because there might be another
            // watch nearby with the same capabilities and accidentally connect to it instead.
            val node = nodes.firstOrNull()
            if (node != null && node.isNearby) {
                _connectedDevice.update { node }
            }
        }
        .launchIn(applicationScope)

    override fun setIsTrackable(isTrackable: Boolean) {
        this.isTrackable.value = isTrackable
    }
}