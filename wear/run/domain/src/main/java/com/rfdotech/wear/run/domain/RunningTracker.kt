@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rfdotech.wear.run.domain

import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.time.Duration

class RunningTracker(
    private val phoneConnector: PhoneConnector,
    private val exerciseTracker: ExerciseTracker,
    applicationScope: CoroutineScope
) {

    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _isTrackable = MutableStateFlow(false)
    val isTrackable = _isTrackable.asStateFlow()

    val distanceMeters = phoneConnector
        .messagingActions
        .filterIsInstance<MessagingAction.DistanceUpdate>()
        .map { it.distanceMeters }
        .stateIn(applicationScope, SharingStarted.Lazily, 0)

    val elapsedTime = phoneConnector
        .messagingActions
        .filterIsInstance<MessagingAction.TimeUpdate>()
        .map { it.elapsedTime }
        .stateIn(applicationScope, SharingStarted.Lazily, Duration.ZERO)

    init {
        phoneConnector
            .messagingActions
            .onEach { action ->
                when (action) {
                    MessagingAction.NotTrackable -> {
                        _isTrackable.update { false }
                    }
                    MessagingAction.Trackable -> {
                        _isTrackable.update { true }
                    }
                    else -> Unit
                }
            }
            .launchIn(applicationScope)

        phoneConnector
            .connectedDevice
            .filterNotNull()
            .onEach {
                exerciseTracker.prepareExercise()
            }
            .launchIn(applicationScope)

        isTracking
            .flatMapLatest {
                if (it) {
                    exerciseTracker.heartRate
                } else flowOf()
            }.onEach { newHeartRate ->
                phoneConnector.sendActionToPhone(MessagingAction.HeartRateUpdate(newHeartRate))
                _heartRate.update { newHeartRate }
            }
            .launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        _isTracking.update { isTracking }
    }
}