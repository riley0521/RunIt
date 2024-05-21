package com.rfdotech.core.data.run

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.rfdotech.core.database.dao.RunPendingSyncDao
import com.rfdotech.core.database.mapper.toRun
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.test_util.MainCoroutineRule
import com.rfdotech.core.test_util.run.FakeRemoteRunDataSource
import com.rfdotech.core.test_util.run.FakeSyncRunScheduler
import com.rfdotech.core.test_util.run.FakeUserStorage
import com.rfdotech.core.test_util.run.run
import io.ktor.client.HttpClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.random.Random
import kotlin.test.Test

class OfflineFirstRunRepositoryTest {

    private lateinit var localRunDataSource: FakeLocalRunDataSource
    private lateinit var remoteRunDataSource: FakeRemoteRunDataSource
    private lateinit var userStorage: FakeUserStorage
    private lateinit var runPendingSyncDao: RunPendingSyncDao
    private lateinit var syncRunScheduler: FakeSyncRunScheduler
    private lateinit var httpClient: HttpClient

    private lateinit var repository: OfflineFirstRunRepository

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    private val testScope = TestScope(mainCoroutineRule.testDispatcher)

    @Before
    fun setup() {
        localRunDataSource = FakeLocalRunDataSource()
        remoteRunDataSource = FakeRemoteRunDataSource()
        userStorage = FakeUserStorage()
        runPendingSyncDao = mockk(relaxed = true)
        syncRunScheduler = FakeSyncRunScheduler()
        httpClient = mockk(relaxed = true)

        runTest {
            userStorage.set(FakeRemoteRunDataSource.FAKE_USER_ID)
        }

        repository = OfflineFirstRunRepository(
            localRunDataSource = localRunDataSource,
            remoteRunDataSource = remoteRunDataSource,
            userStorage = userStorage,
            runPendingSyncDao = runPendingSyncDao,
            syncRunScheduler = syncRunScheduler,
            httpClient = httpClient,
            applicationScope = testScope.backgroundScope
        )
    }

    @Test
    fun testGetAllLocal() = testScope.runTest {
        localRunDataSource.upsert(run(id = "111"))

        assertThat(repository.getAllLocal().first()).isNotEmpty()
    }

    @Test
    fun testFetchFromRemote_HappyPath() = testScope.runTest {
        remoteRunDataSource.upsert(
            userId = FakeRemoteRunDataSource.FAKE_USER_ID,
            run = run(id = "111"),
            mapPicture = getRandomBytes()
        )
        repository = OfflineFirstRunRepository(
            localRunDataSource = localRunDataSource,
            remoteRunDataSource = remoteRunDataSource,
            userStorage = userStorage,
            runPendingSyncDao = runPendingSyncDao,
            syncRunScheduler = syncRunScheduler,
            httpClient = httpClient,
            applicationScope = backgroundScope
        )

        val result = repository.fetchFromRemote()

        assertThat(result is Result.Success).isTrue()
        assertThat(localRunDataSource.getAll().first()).isNotEmpty()
    }

    @Test
    fun testFetchFromRemote_UnhappyPath_NoUserId() = testScope.runTest {
        remoteRunDataSource.upsert(
            userId = FakeRemoteRunDataSource.FAKE_USER_ID,
            run = run(id = "111"),
            mapPicture = getRandomBytes()
        )
        repository = OfflineFirstRunRepository(
            localRunDataSource = localRunDataSource,
            remoteRunDataSource = remoteRunDataSource,
            userStorage = userStorage,
            runPendingSyncDao = runPendingSyncDao,
            syncRunScheduler = syncRunScheduler,
            httpClient = httpClient,
            applicationScope = backgroundScope
        )
        userStorage.set(null)

        val result = repository.fetchFromRemote()

        assertThat(result is Result.Error).isTrue()
    }

    @Test
    fun testFetchFromRemote_UnhappyPath_ServerError() = testScope.runTest {
        remoteRunDataSource.upsert(
            userId = FakeRemoteRunDataSource.FAKE_USER_ID,
            run = run(id = "111"),
            mapPicture = getRandomBytes()
        )
        repository = OfflineFirstRunRepository(
            localRunDataSource = localRunDataSource,
            remoteRunDataSource = remoteRunDataSource,
            userStorage = userStorage,
            runPendingSyncDao = runPendingSyncDao,
            syncRunScheduler = syncRunScheduler,
            httpClient = httpClient,
            applicationScope = backgroundScope
        )
        remoteRunDataSource.error = true

        val result = repository.fetchFromRemote()

        assertThat(result is Result.Error).isTrue()
    }

    @Test
    fun testUpsert_UnhappyPath_DiskIsFull() = testScope.runTest {
        localRunDataSource.error = true
        val result = repository.upsert(run(id = "111"), getRandomBytes())
        assertThat(result is Result.Error).isTrue()
    }

    @Test
    fun testUpsert_UnhappyPath_ServerError_SaveToPendingRuns() = testScope.runTest {
        remoteRunDataSource.error = true

        val result = repository.upsert(run(id = "111"), getRandomBytes())

        assertThat(result is Result.Success).isTrue()

        remoteRunDataSource.error = false
        (remoteRunDataSource.getAll(FakeRemoteRunDataSource.FAKE_USER_ID) as Result.Success).also {
            assertThat(it.data).isEmpty()
        }

        assertThat(syncRunScheduler.isCreateRunWorkerScheduled).isTrue()
    }

    @Test
    fun testUpsert_HappyPath() = testScope.runTest {
        val result = repository.upsert(run(id = "123"), getRandomBytes())

        assertThat(result is Result.Success).isTrue()
        (remoteRunDataSource.getAll(FakeRemoteRunDataSource.FAKE_USER_ID) as Result.Success).also {
            assertThat(it.data.size).isEqualTo(1)
        }

        val run = localRunDataSource.getAll().first()[0]
        assertThat(run.mapPictureUrl).isNotNull()
    }

    @Test
    fun testDeleteAll() = testScope.runTest {
        localRunDataSource.upsertMultiple(
            listOf(run(id = "111"), run(id = "112"))
        )

        assertThat(repository.getAllLocal().first().size).isEqualTo(2)

        repository.deleteAllFromLocal()

        assertThat(repository.getAllLocal().first()).isEmpty()
    }

    @Test
    fun testDeleteById_HappyPath_RunIsPending_DeleteRun() = testScope.runTest {
        localRunDataSource.upsert(run(id = "111"))

        coEvery { runPendingSyncDao.getRunPendingSyncEntity("111") } answers {
            runPendingSyncEntity(
                id = "111",
                userId = FakeRemoteRunDataSource.FAKE_USER_ID,
                mapPictureBytes = getRandomBytes()
            )
        }

        repository.deleteById("111")
        coVerify {
            runPendingSyncDao.deleteRunPendingSyncEntity("111")
        }
    }

    @Test
    fun testDeleteById_HappyPath() = testScope.runTest {
        val newRun = run(id = "111")
        localRunDataSource.upsert(newRun)
        remoteRunDataSource.upsert(FakeRemoteRunDataSource.FAKE_USER_ID, newRun, getRandomBytes())

        repository.deleteById("111")

        assertThat(syncRunScheduler.isDeleteRunWorkerScheduled).isFalse()
        (remoteRunDataSource.getAll(FakeRemoteRunDataSource.FAKE_USER_ID) as Result.Success).also {
            assertThat(it.data.size).isEqualTo(1)
        }
    }

    @Test
    fun testDeleteById_UnhappyPath_ServerError_ScheduleDelete() = testScope.runTest {
        val newRun = run(id = "111")
        localRunDataSource.upsert(newRun)
        remoteRunDataSource.upsert(FakeRemoteRunDataSource.FAKE_USER_ID, newRun, getRandomBytes())

        // We need this because mockk returns an instance of RunPendingSyncEntity instead of null.
        coEvery { runPendingSyncDao.getRunPendingSyncEntity("111") } answers { null }

        remoteRunDataSource.error = true
        repository.deleteById("111")

        assertThat(syncRunScheduler.isDeleteRunWorkerScheduled).isTrue()
    }

    @Test
    fun testSyncPendingRuns() = testScope.runTest {
        val pendingRun = runPendingSyncEntity(id = "112", userId = FakeRemoteRunDataSource.FAKE_USER_ID, mapPictureBytes = getRandomBytes())

        coEvery { runPendingSyncDao.getAllRunPendingSyncEntities(FakeRemoteRunDataSource.FAKE_USER_ID) } answers {
            listOf(
                pendingRun
            )
        }
        coEvery { runPendingSyncDao.getAllDeletedRunSyncEntities(FakeRemoteRunDataSource.FAKE_USER_ID) } answers {
            listOf(
                deletedRunSyncEntity(runId = "113", FakeRemoteRunDataSource.FAKE_USER_ID)
            )
        }

        repository.syncPendingRuns()

        coVerify {
            val run = pendingRun.run.toRun()
            remoteRunDataSource.upsert(FakeRemoteRunDataSource.FAKE_USER_ID, run, pendingRun.mapPictureBytes)
            runPendingSyncDao.deleteRunPendingSyncEntity("112")
        }

        coVerify {
            remoteRunDataSource.deleteById("113")
            runPendingSyncDao.deleteDeletedRunSyncEntity("113")
        }
    }

    private fun getRandomBytes(): ByteArray = Random.nextBytes(128)
}