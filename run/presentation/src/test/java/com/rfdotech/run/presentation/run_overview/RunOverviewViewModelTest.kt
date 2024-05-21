package com.rfdotech.run.presentation.run_overview

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.rfdotech.core.domain.run.SyncRunScheduler.SyncType
import com.rfdotech.core.presentation.ui.auth.GoogleAuthUiClient
import com.rfdotech.core.test_util.MainCoroutineRule
import com.rfdotech.core.test_util.run.FakeSyncRunScheduler
import com.rfdotech.core.test_util.run.FakeUserStorage
import com.rfdotech.run.presentation.active_run.FakeRunRepository
import com.rfdotech.core.test_util.run.run
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class RunOverviewViewModelTest {

    private lateinit var runRepository: FakeRunRepository
    private lateinit var syncRunScheduler: FakeSyncRunScheduler
    private lateinit var userStorage: FakeUserStorage
    private lateinit var googleAuthUiClient: GoogleAuthUiClient

    private lateinit var viewModel: RunOverviewViewModel

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    private val testScope = TestScope(mainCoroutineRule.testDispatcher)

    @Before
    fun setup() {
        runRepository = FakeRunRepository()
        syncRunScheduler = FakeSyncRunScheduler()
        userStorage = FakeUserStorage()
        googleAuthUiClient = mockk(relaxed = true)

        viewModel = RunOverviewViewModel(
            runRepository = runRepository,
            syncRunScheduler = syncRunScheduler,
            userStorage = userStorage,
            googleAuthUiClient = googleAuthUiClient,
            applicationScope = testScope.backgroundScope
        )
    }

    @Test
    fun testInitialization() = testScope.runTest {
        runRepository = mockk(relaxed = true)
        viewModel = RunOverviewViewModel(
            runRepository = runRepository,
            syncRunScheduler = syncRunScheduler,
            userStorage = userStorage,
            googleAuthUiClient = googleAuthUiClient,
            applicationScope = backgroundScope
        )

        advanceUntilIdle()

        assertThat(syncRunScheduler.isFetchRunsWorkerScheduled).isTrue()
        coVerify {
            runRepository.getAllLocal()
            runRepository.syncPendingRuns()
            runRepository.fetchFromRemote()
        }
    }

    @Test
    fun testSignOut() = testScope.runTest {
        runRepository.fetchFromRemote()
        syncRunScheduler.apply {
            scheduleSync(SyncType.CreateRun(run(id = "123"), Random.nextBytes(128)))
            scheduleSync(SyncType.DeleteRun("123"))
            scheduleSync(SyncType.FetchRuns(30.minutes))
        }

        // Verify that runs are not empty first before signing out.
        assertThat(runRepository.getAllLocal().first()).isNotEmpty()

        // Trigger the signOut() in viewModel
        viewModel.onAction(RunOverviewAction.OnSignOutClick)

        advanceTimeBy(100L)

        with(syncRunScheduler) {
            assertThat(isCreateRunWorkerScheduled).isFalse()
            assertThat(isDeleteRunWorkerScheduled).isFalse()
            assertThat(isFetchRunsWorkerScheduled).isFalse()
        }
        assertThat(runRepository.getAllLocal().first()).isEmpty()
        assertThat(userStorage.get()).isNull()
        coVerify {
            googleAuthUiClient.signOut()
        }
    }

    @Test
    fun testDeleteRun() = testScope.runTest {
        val newRun = run(id = "222")
        runRepository.upsert(newRun, Random.nextBytes(128))

        // Verify that runs are not empty first before trying to delete it.
        assertThat(runRepository.getAllLocal().first().size).isEqualTo(3)

        viewModel.onAction(RunOverviewAction.DeleteRunById("222"))

        advanceUntilIdle()

        assertThat(runRepository.getAllLocal().first().size).isEqualTo(2)
    }
}
