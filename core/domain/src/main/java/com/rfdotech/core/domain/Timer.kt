package com.rfdotech.core.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Timer {

    fun timeAndEmit(clock: Clock = Clock.systemDefaultZone()): Flow<Duration> {
        return flow {
            var lastEmitTime = clock.millis()
            while(true) {
                delay(200L)
                val currentTime = clock.millis()
                val elapsedTime = currentTime - lastEmitTime
                emit(elapsedTime.milliseconds)

                lastEmitTime = currentTime
            }
        }
    }
}