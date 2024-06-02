package com.rfdotech.core.connectivity.data

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Wearable
import com.rfdotech.core.connectivity.domain.DeviceNode
import com.rfdotech.core.connectivity.domain.DeviceType
import com.rfdotech.core.connectivity.domain.NodeDiscovery
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class WearNodeDiscovery(
    context: Context
): NodeDiscovery {

    private val capabilityClient = Wearable.getCapabilityClient(context)

    override fun observeConnectedDevices(
        localDeviceType: DeviceType
    ): Flow<Set<DeviceNode>> = callbackFlow {
        val remoteCapability = when (localDeviceType) {
            DeviceType.PHONE -> "runit_wear_capability"
            DeviceType.WATCH -> "runit_phone_capability"
        }

        try {
            val capability = capabilityClient
                .getCapability(remoteCapability, CapabilityClient.FILTER_REACHABLE)
                .await()

            val connectedDevices = capability.nodes.map { it.toDeviceNode() }.toSet()
            send(connectedDevices)
        } catch (e: ApiException) {
            awaitClose()
            return@callbackFlow
        }

        val listener: (CapabilityInfo) -> Unit = { capability ->
            trySend(capability.nodes.map { it.toDeviceNode() }.toSet())
        }
        capabilityClient.addListener(listener, remoteCapability)

        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }
}