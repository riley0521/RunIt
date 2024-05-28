package com.rfdotech.run.data

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.rfdotech.core.database.dao.RunPendingSyncDao
import com.rfdotech.core.database.entity.RunEntity
import com.rfdotech.core.database.entity.RunPendingSyncEntity
import com.rfdotech.core.domain.ZonedDateTimeHelper
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.test_util.run.FakeRemoteRunDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class CreateRunWorkerTest {

    private lateinit var context: Context

    private lateinit var remoteRunDataSource: FakeRemoteRunDataSource
    private lateinit var runPendingSyncDao: RunPendingSyncDao
    private lateinit var testWorkerFactory: TestWorkerFactory

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(
            context,
            config
        )

        remoteRunDataSource = FakeRemoteRunDataSource()
        runPendingSyncDao = mockk(relaxed = true)
        testWorkerFactory = TestWorkerFactory(
            remoteRunDataSource = remoteRunDataSource,
            runPendingSyncDao = runPendingSyncDao
        )
    }

    @Test
    fun testCreateRunWorker_HappyPath() = runTest {
        val fakeRunId = "111"

        coEvery { runPendingSyncDao.getRunPendingSyncEntity(fakeRunId) } answers {
            runPendingSyncEntity(id = fakeRunId, userId = FakeRemoteRunDataSource.FAKE_USER_ID)
        }

        val input = workDataOf(
            CreateRunWorker.RUN_ID to fakeRunId,
            CreateRunWorker.USER_ID to FakeRemoteRunDataSource.FAKE_USER_ID
        )

        val request = TestListenableWorkerBuilder<CreateRunWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .setInputData(input)
            .build()

        val result = request.doWork()

        assertThat(result is ListenableWorker.Result.Success).isTrue()
        (remoteRunDataSource.getAll(FakeRemoteRunDataSource.FAKE_USER_ID) as Result.Success).also {
            assertThat(it.data).isNotEmpty()
        }
        coVerify {
            runPendingSyncDao.deleteRunPendingSyncEntity(fakeRunId)
        }
    }

    @Test
    fun testCreateRunWorker_UnhappyPath_RunPendingSyncEntityNotFound() = runTest {
        val fakeRunId = "111"

        coEvery { runPendingSyncDao.getRunPendingSyncEntity(fakeRunId) } answers {
            null
        }

        val input = workDataOf(
            CreateRunWorker.RUN_ID to fakeRunId,
            CreateRunWorker.USER_ID to FakeRemoteRunDataSource.FAKE_USER_ID
        )

        val request = TestListenableWorkerBuilder<CreateRunWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .setInputData(input)
            .build()

        val result = request.doWork()

        assertThat(result is ListenableWorker.Result.Failure).isTrue()
    }

    @Test
    fun testCreateRunWorker_UnhappyPath_ServerError() = runTest {
        val fakeRunId = "111"

        coEvery { runPendingSyncDao.getRunPendingSyncEntity(fakeRunId) } answers {
            runPendingSyncEntity(id = fakeRunId, userId = FakeRemoteRunDataSource.FAKE_USER_ID)
        }

        val input = workDataOf(
            CreateRunWorker.RUN_ID to fakeRunId,
            CreateRunWorker.USER_ID to FakeRemoteRunDataSource.FAKE_USER_ID
        )

        val request = TestListenableWorkerBuilder<CreateRunWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .setInputData(input)
            .build()

        remoteRunDataSource.error = true
        val result = request.doWork()

        assertThat(result is ListenableWorker.Result.Retry).isTrue()
    }

    private fun runPendingSyncEntity(
        id: String,
        userId: String,
        mapPictureBytes: ByteArray = Random.nextBytes(128),
        duration: Duration = 30.minutes
    ): RunPendingSyncEntity {
        return RunPendingSyncEntity(
            run = RunEntity(
                durationMillis = duration.inWholeMilliseconds,
                distanceMeters = 2500,
                dateTimeUtc = ZonedDateTimeHelper.addZoneIdToZonedDateTime(ZonedDateTime.now()),
                latitude = 1.0,
                longitude = 1.0,
                avgSpeedKmh = 10.5,
                maxSpeedKmh = 15.0,
                totalElevationMeters = 1,
                numberOfSteps = 8500,
                mapPictureUrl = null,
                id = id
            ), runId = id, mapPictureBytes = mapPictureBytes, userId = userId
        )
    }

    class TestWorkerFactory(
        private val remoteRunDataSource: FakeRemoteRunDataSource,
        private val runPendingSyncDao: RunPendingSyncDao
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? {
            return CreateRunWorker(
                context = appContext,
                params = workerParameters,
                remoteRunDataSource = remoteRunDataSource,
                runPendingSyncDao = runPendingSyncDao
            )
        }
    }
}