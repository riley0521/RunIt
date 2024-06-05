package com.rfdotech.run.presentation.active_run

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNull
import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.test_util.MainCoroutineRule
import com.rfdotech.core.test_util.MutableClock
import com.rfdotech.core.test_util.advanceTimeBy
import com.rfdotech.run.domain.RunningTracker
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class RunningTrackerTest {

    private lateinit var locationObserver: FakeLocationObserver
    private lateinit var stepObserver: FakeStepObserver
    private lateinit var watchConnector: FakePhoneToWatchConnector
    private lateinit var mutableClock: MutableClock
    private lateinit var runningTracker: RunningTracker

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    private val testScope = TestScope(mainCoroutineRule.testDispatcher)

    @Before
    fun setup() {
        stepObserver = FakeStepObserver()
        watchConnector = FakePhoneToWatchConnector()
        mutableClock = MutableClock(Clock.systemDefaultZone())
        locationObserver = FakeLocationObserver()

        runningTracker = RunningTracker(
            locationObserver = locationObserver,
            stepObserver = stepObserver,
            watchConnector = watchConnector,
            applicationScope = testScope.backgroundScope,
            clock = mutableClock
        )
    }

    @Test
    fun startObservingLocation_SeeCurrentLocation() = testScope.runTest {
        runningTracker.startObservingLocation()

        runningTracker.currentLocation.test {
            val initial = awaitItem()
            assertThat(initial).isNull()

            val emit1 = awaitItem()
            assertThat(emit1).isEqualTo(FAKE_LOCATION)
        }
    }

    @Test
    fun startTrackingTest() = testScope.runTest {
        turbineScope {
            val elapsedTimeFlow = runningTracker.elapsedTime.testIn(backgroundScope)
            val currentLocationFlow = runningTracker.currentLocation.testIn(backgroundScope)
            val runDataFlow = runningTracker.runData.testIn(backgroundScope)

            runningTracker.startObservingLocation()
            runningTracker.setIsTracking(true)
            currentLocationFlow.skipItems(1)

            val initialTime = elapsedTimeFlow.awaitItem()
            assertThat(initialTime).isEqualTo(Duration.ZERO)

            val firstLocation = currentLocationFlow.awaitItem()
            assertThat(firstLocation).isEqualTo(FAKE_LOCATION)

            mutableClock.advanceTimeBy(10.seconds.toJavaDuration())
            locationObserver.emitAnotherLocation()
            advanceTimeBy(1.seconds, mutableClock)

            // Skip the initial and the runData with 1 location only
            // because we need a runData with at least 2 location to compute the pace and distanceInMeters
            runDataFlow.skipItems(2)

            val runDataInfo = runDataFlow.awaitItem()
            assertThat(runDataInfo.locations.last().size).isEqualTo(2)
            assertThat(runDataInfo.locations.last().last().durationTimestamp).isNotEqualTo(Duration.ZERO)
            assertThat(runDataInfo.paceInSeconds.inWholeSeconds).isGreaterThan(0)
            assertThat(runDataInfo.distanceMeters).isGreaterThan(0)

            // Ignore the remaining emission to avoid exception.
            elapsedTimeFlow.cancelAndIgnoreRemainingEvents()
            currentLocationFlow.cancelAndIgnoreRemainingEvents()
            runDataFlow.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testObserveStep() = testScope.runTest {
        runningTracker.setIsTracking(true)
        advanceUntilIdle()

        runningTracker.stepCount.test {
            awaitItem() // Initial emission.

            repeat(4) {
                stepObserver.incrementStep()
            }

            // Even if we pause, the step count should not be reset to 0.
            runningTracker.setIsTracking(false)
            advanceUntilIdle()

            runningTracker.setIsTracking(true)
            advanceUntilIdle()

            stepObserver.incrementStep()

            skipItems(3)
            val currentStepCount = awaitItem()
            assertThat(currentStepCount).isEqualTo(4)
        }
    }

    @Test
    fun testListenToHeartRateUpdatesFromWatch() = testScope.runTest {
        turbineScope {
            val runDataFlow = runningTracker.runData.testIn(backgroundScope)

            runningTracker.startObservingLocation()
            runningTracker.setIsTracking(true)

            locationObserver.emitAnotherLocation()
            watchConnector.sendActionToThis(MessagingAction.HeartRateUpdate(150))
            advanceTimeBy(1.seconds, mutableClock)

            runDataFlow.skipItems(1)
            val em2 = runDataFlow.awaitItem()

            assertThat(em2.heartRates).isNotEmpty()
            assertThat(em2.heartRates.firstOrNull()).isEqualTo(150)
        }
    }
}