package com.rfdotech.run.presentation.active_run

import app.cash.turbine.turbineScope
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNull
import com.rfdotech.core.domain.location.LocationWithAltitude
import com.rfdotech.core.test_util.MutableClock
import com.rfdotech.core.test_util.advanceTimeBy
import com.rfdotech.run.domain.RunData
import com.rfdotech.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class RunningTrackerTest {

    private lateinit var locationObserver: FakeLocationObserver
    private lateinit var mutableClock: MutableClock
    private lateinit var runningTracker: RunningTracker

    @Before
    fun setup() {
        mutableClock = MutableClock(Clock.systemDefaultZone())
    }

    @Test
    fun startObservingLocation_SeeCurrentLocation() = runTest {
        locationObserver = FakeLocationObserver()
        runningTracker = RunningTracker(
            locationObserver = locationObserver,
            applicationScope = backgroundScope,
            clock = mutableClock
        )

        val currentLocation = Channel<LocationWithAltitude?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            runningTracker.currentLocation.collect {
                currentLocation.send(it)
            }
        }
        runningTracker.startObservingLocation()

        val initial = currentLocation.receive()
        assertThat(initial).isNull()

        val emit1 = currentLocation.receive()
        assertThat(emit1).isEqualTo(FAKE_LOCATION)
    }

    @Test
    fun startTrackingTest() = runTest {
        locationObserver = FakeLocationObserver()
        runningTracker = RunningTracker(
            locationObserver = locationObserver,
            applicationScope = backgroundScope,
            clock = mutableClock
        )

        val elapsedTime = Channel<Duration>()
        val currentLocation = Channel<LocationWithAltitude?>()
        val runData = Channel<RunData>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            runningTracker.elapsedTime.collect {
                elapsedTime.send(it)
            }
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            runningTracker.currentLocation.collect {
                currentLocation.send(it)
            }
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            runningTracker.runData.collect {
                runData.send(it)
            }
        }

        turbineScope {
            val elapsedTimeFlow = elapsedTime.receiveAsFlow().testIn(backgroundScope)
            val currentLocationFlow = currentLocation.receiveAsFlow().testIn(backgroundScope)
            val runDataFlow = runData.receiveAsFlow().testIn(backgroundScope)

            runningTracker.startObservingLocation()
            runningTracker.setIsTracking(true)
            currentLocationFlow.skipItems(1)

            val initialTime = elapsedTimeFlow.awaitItem()
            assertThat(initialTime).isEqualTo(Duration.ZERO)

            val firstLocation = currentLocationFlow.awaitItem()
            assertThat(firstLocation).isEqualTo(FAKE_LOCATION)

            mutableClock.advanceTimeBy(1.seconds.toJavaDuration())
            locationObserver.emitAnotherLocation()
            advanceTimeBy(1.seconds, mutableClock)

            // Skip the initial and the runData with 1 location only
            // because we need a runData with at least 2 location to compute the pace and distanceInMeters
            runDataFlow.skipItems(2)

            val runDataInfo = runDataFlow.awaitItem()
            assertThat(runDataInfo.locations.last().size).isEqualTo(2)
            assertThat(runDataInfo.locations.last().last().durationTimestamp).isNotEqualTo(Duration.ZERO)
            assertThat(runDataInfo.paceInSeconds).isEqualTo(11.seconds)
            assertThat(runDataInfo.distanceMeters).isEqualTo(88)

            // Ignore the remaining emission to avoid exception.
            elapsedTimeFlow.cancelAndIgnoreRemainingEvents()
            currentLocationFlow.cancelAndIgnoreRemainingEvents()
            runDataFlow.cancelAndIgnoreRemainingEvents()
        }
    }
}