package com.rfdotech.run.presentation.active_run

import com.rfdotech.core.connectivity.domain.DeviceNode
import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.connectivity.domain.messaging.MessagingError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import com.rfdotech.run.domain.WatchConnector
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class FakePhoneToWatchConnector(
    private val deviceNode: DeviceNode? = null
) : WatchConnector {
    override val connectedDevice: StateFlow<DeviceNode?>
        get() = MutableStateFlow(deviceNode)

    private val _messagingActions = Channel<MessagingAction>()
    override val messagingActions: Flow<MessagingAction>
        get() = _messagingActions.receiveAsFlow()

    var isTrackable = false
    var error = false

    suspend fun sendActionToThis(action: MessagingAction) {
        _messagingActions.send(action)
    }

    private val _actionsSentToWatch = mutableListOf<MessagingAction>()
    val actionsSentToWatch: List<MessagingAction> = _actionsSentToWatch

    override suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError> {
        return if (error) {
            Result.Error(MessagingError.DISCONNECTED)
        } else {
            _actionsSentToWatch.add(action)
            Result.Success(Unit)
        }
    }

    override fun setIsTrackable(isTrackable: Boolean) {
        this.isTrackable = isTrackable
    }
}