package com.rfdotech.run.presentation.active_run

import app.cash.turbine.turbineScope
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import com.rfdotech.core.connectivity.domain.messaging.MessagingAction
import com.rfdotech.core.test_util.MainCoroutineRule
import com.rfdotech.core.test_util.MutableClock
import com.rfdotech.core.test_util.advanceTimeBy
import com.rfdotech.run.domain.RunData
import com.rfdotech.run.domain.RunningTracker
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import java.time.Clock
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class ActiveRunViewModelTest {

    private lateinit var runRepository: FakeRunRepository

    private lateinit var locationObserver: FakeLocationObserver
    private lateinit var stepObserver: FakeStepObserver
    private lateinit var mutableClock: MutableClock
    private lateinit var runningTracker: RunningTracker
    private lateinit var watchConnector: FakePhoneToWatchConnector
    private lateinit var viewModel: ActiveRunViewModel

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    private val testScope = TestScope(mainCoroutineRule.testDispatcher)

    @Before
    fun setup() {
        runRepository = FakeRunRepository()
        locationObserver = FakeLocationObserver()
        stepObserver = FakeStepObserver()
        mutableClock = MutableClock(Clock.systemDefaultZone())
        watchConnector = FakePhoneToWatchConnector()

        runningTracker = RunningTracker(
            locationObserver = locationObserver,
            stepObserver = stepObserver,
            watchConnector = watchConnector,
            applicationScope = testScope.backgroundScope,
            clock = mutableClock
        )
        viewModel = ActiveRunViewModel(
            runningTracker = runningTracker,
            runRepository = runRepository,
            watchConnector = watchConnector,
            applicationScope = testScope.backgroundScope
        )
    }

    @Test
    fun testUserHasPermission_HasStartedRunning_ShouldTrack() = testScope.runTest {

        turbineScope {
            assertThat(viewModel.state).isEqualTo(ActiveRunState())

            viewModel.onAction(ActiveRunAction.SubmitLocationPermissionInfo(true, false))
            viewModel.onAction(ActiveRunAction.OnToggleRunClick)

            val currentLocation = runningTracker.currentLocation.testIn(backgroundScope)
            currentLocation.skipItems(1) // Skip initial

            val firstLocation = currentLocation.awaitItem()
            assertThat(firstLocation).isEqualTo(FAKE_LOCATION)

            val runDataFlow = runningTracker.runData.testIn(backgroundScope)
            runDataFlow.skipItems(1) // Skip initial

            mutableClock.advanceTimeBy(1.seconds.toJavaDuration())
            locationObserver.emitAnotherLocation()
            advanceTimeBy(1.seconds, mutableClock)

            runDataFlow.skipItems(1)// Skip the 2nd emission with 1 location because we don't need that.

            with(viewModel.state) {
                println("Elapsed: $elapsedTime")
                assertThat(elapsedTime).isNotEqualTo(Duration.ZERO)
                assertThat(runData).isEqualTo(runDataFlow.awaitItem())
                assertThat(hasStartedRunning).isTrue()
                assertThat(shouldTrack).isTrue()
                assertThat(currentLocation.awaitItem()).isEqualTo(FAKE_LOCATION_2)
                assertThat(showLocationRationale).isFalse()
                assertThat(showPostNotificationRationale).isFalse()
                assertThat(isRunFinished).isFalse()
                assertThat(isSavingRun).isFalse()
            }

            viewModel.onAction(ActiveRunAction.OnFinishRunClick)
            viewModel.onAction(ActiveRunAction.OnRunProcessed(mapPictureBytes = Random.nextBytes(128)))

            with(viewModel.state) {
                assertThat(isRunFinished).isTrue()
                assertThat(isSavingRun).isTrue()
            }

            val events = viewModel.events.testIn(backgroundScope)
            val isTrackingFlow = runningTracker.isTracking.testIn(backgroundScope)

            assertThat(runDataFlow.awaitItem()).isEqualTo(RunData())
            assertThat(isTrackingFlow.expectMostRecentItem()).isFalse()
            assertThat(events.awaitItem()).isEqualTo(ActiveRunEvent.RunSaved)
            assertThat(viewModel.state.isSavingRun).isFalse()
            assertThat(runRepository.isNotEmpty()).isTrue()

            runDataFlow.cancelAndIgnoreRemainingEvents()

            /**
             * Investigation: We have a bug in elapsedTime flow in RunningTracker
             * where in if you call finishRun() it will emit 0s and then it will emit the previous elapsed time again like 2s
             * It is ignored in viewModel.
             */
        }
    }

    @Test
    fun testUserHasStartedRunning_ClickBackButton_OnResume() = testScope.runTest {

        viewModel.onAction(ActiveRunAction.SubmitLocationPermissionInfo(true, false))
        viewModel.onAction(ActiveRunAction.OnToggleRunClick)

        with(viewModel.state) {
            assertThat(hasStartedRunning).isTrue()
            assertThat(shouldTrack).isTrue()
        }

        viewModel.onAction(ActiveRunAction.OnBackClick)
        assertThat(viewModel.state.shouldTrack).isFalse()

        viewModel.onAction(ActiveRunAction.OnResumeRunClick)
        assertThat(viewModel.state.shouldTrack).isTrue()
    }

    @Test
    fun testUserHasStartedRunning_ListenToWatchActions_ConnectionRequest() = testScope.runTest {
        watchConnector = FakePhoneToWatchConnector()
        viewModel = ActiveRunViewModel(
            runningTracker = runningTracker,
            runRepository = runRepository,
            watchConnector = watchConnector,
            applicationScope = backgroundScope
        )

        viewModel.onAction(ActiveRunAction.SubmitLocationPermissionInfo(true, false))
        viewModel.onAction(ActiveRunAction.OnToggleRunClick)
        advanceUntilIdle()

        watchConnector.sendActionToThis(MessagingAction.ConnectionRequest)
        advanceUntilIdle()

        assertThat(watchConnector.actionsSentToWatch[0] is MessagingAction.Pause).isTrue()
        assertThat(watchConnector.actionsSentToWatch[1] is MessagingAction.StartOrResumeRun).isTrue()
    }

    @Test
    fun testSendActionToWatch() = testScope.runTest {
        watchConnector = mockk(relaxed = true)
        viewModel = ActiveRunViewModel(
            runningTracker = runningTracker,
            runRepository = runRepository,
            watchConnector = watchConnector,
            applicationScope = backgroundScope
        )

        viewModel.onAction(ActiveRunAction.OnFinishRunClick)
        advanceUntilIdle()

        coVerify { watchConnector.sendActionToWatch(MessagingAction.Finish) }

        viewModel.onAction(ActiveRunAction.OnResumeRunClick)
        advanceUntilIdle()

        coVerify { watchConnector.sendActionToWatch(MessagingAction.StartOrResumeRun) }

        viewModel.onAction(ActiveRunAction.OnToggleRunClick)
        advanceUntilIdle()

        coVerify { watchConnector.sendActionToWatch(MessagingAction.StartOrResumeRun) }

        viewModel.onAction(ActiveRunAction.OnToggleRunClick)
        advanceUntilIdle()

        coVerify { watchConnector.sendActionToWatch(MessagingAction.Pause) }
    }

    @Test
    fun testUserDeniesPostNotificationPermissionOnce_ShowRationale_DismissRationale() = testScope.runTest {

        viewModel.onAction(ActiveRunAction.SubmitPostNotificationPermissionInfo(false, true))
        assertThat(viewModel.state.showPostNotificationRationale).isTrue()
        assertThat(viewModel.state.showLocationRationale).isFalse()

        viewModel.onAction(ActiveRunAction.DismissRationaleDialog)
        assertThat(viewModel.state.showPostNotificationRationale).isFalse()
        assertThat(viewModel.state.showLocationRationale).isFalse()
    }

    @Test
    fun testUserDeniesLocationPermissionOnce_ShowRationale_DismissRationale() = testScope.runTest {

        viewModel.onAction(ActiveRunAction.SubmitLocationPermissionInfo(false, true))
        assertThat(viewModel.state.showPostNotificationRationale).isFalse()
        assertThat(viewModel.state.showLocationRationale).isTrue()

        viewModel.onAction(ActiveRunAction.DismissRationaleDialog)
        assertThat(viewModel.state.showPostNotificationRationale).isFalse()
        assertThat(viewModel.state.showLocationRationale).isFalse()
    }
}