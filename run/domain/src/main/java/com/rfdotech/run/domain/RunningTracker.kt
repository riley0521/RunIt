package com.rfdotech.run.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class RunningTracker(
    private val locationObserver: LocationObserver,
    applicationScope: CoroutineScope
) {

    companion object {
        private const val OBSERVE_LOCATION_INTERVAL = 1000L
    }

    private val isObservingLocation = MutableStateFlow(false)

    val currentLocation = isObservingLocation
        .flatMapLatest {
            if (it) {
                locationObserver.observeLocation(OBSERVE_LOCATION_INTERVAL)
            } else flowOf()
        }
        .stateIn(applicationScope, SharingStarted.Lazily, null)

    fun startObservingLocation() {
        isObservingLocation.update { true }
    }

    fun stopObservingLocation() {
        isObservingLocation.update { false }
    }
}