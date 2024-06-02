package com.rfdotech.wear.run.domain

import com.rfdotech.core.connectivity.domain.DeviceNode
import kotlinx.coroutines.flow.StateFlow

interface PhoneConnector {
    val connectedDevice: StateFlow<DeviceNode?>
}