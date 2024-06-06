package com.rfdotech.run.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.rfdotech.core.database.dao.RunPendingSyncDao
import com.rfdotech.core.database.entity.DeletedRunSyncEntity
import com.rfdotech.core.database.entity.RunPendingSyncEntity
import com.rfdotech.core.database.mapper.toRunEntity
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.auth.UserStorage
import com.rfdotech.core.domain.run.DELETE_ALL_RUNS_UNIQUE_WORK
import com.rfdotech.core.domain.run.FETCH_RUNS_INTERVAL
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.run.SyncRunScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler.SyncType
import com.rfdotech.core.domain.run.WorkInformation
import com.rfdotech.run.data.mapper.toWorkInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerScheduler(
    private val context: Context,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val userStorage: UserStorage,
    private val dispatcherProvider: DispatcherProvider,
    private val applicationScope: CoroutineScope
) : SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)
    private var deleteAllRunsId: UUID? = null

    override suspend fun scheduleSync(type: SyncType) {
        when (type) {
            is SyncType.CreateRun -> scheduleCreateRunWorker(type.run, type.mapPictureBytes)
            is SyncType.DeleteRun -> scheduleDeleteRunWorker(type.runId)
            is SyncType.FetchRuns -> scheduleFetchRunsWorker(type.interval)
            SyncType.DeleteRunsFromRemoteDb -> scheduleDeleteRunsFromRemoteDb()
        }
    }

    @SuppressLint("NewApi") // We have desugaring enabled so we are safe to use Duration and Dates
    private suspend fun scheduleFetchRunsWorker(interval: Duration) {
        val isSyncScheduled = withContext(dispatcherProvider.io) {
            workManager
                .getWorkInfosByTag(FETCH_RUNS_WORKER_TAG)
                .get()
                .isNotEmpty()
        }
        if (isSyncScheduled) {
            return
        }

        val duration = interval.toJavaDuration()
        val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(repeatInterval = duration)
            .setConstraints(getConstraints())
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = BACK_OFF_DELAY_MILLIS,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInitialDelay(
                duration = FETCH_RUNS_INTERVAL.toJavaDuration()
            )
            .addTag(FETCH_RUNS_WORKER_TAG)
            .build()

        workManager.enqueue(workRequest).await()
    }

    private suspend fun scheduleCreateRunWorker(run: Run, mapPictureBytes: ByteArray) {
        val userId = userStorage.get() ?: return

        val pendingRun = RunPendingSyncEntity(
            run = run.toRunEntity(),
            mapPictureBytes = mapPictureBytes,
            userId = userId
        )
        runPendingSyncDao.upsertRunPendingSyncEntity(pendingRun)

        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .addTag(CREATE_RUN_WORKER_TAG)
            .setConstraints(getConstraints())
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = BACK_OFF_DELAY_MILLIS,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(CreateRunWorker.USER_ID, userId)
                    .putString(CreateRunWorker.RUN_ID, pendingRun.runId)
                    .build()
            )
            .build()

        applicationScope.async {
            workManager.enqueue(workRequest).await()
        }.await()
    }

    private suspend fun scheduleDeleteRunWorker(runId: RunId) {
        val userId = userStorage.get() ?: return

        val pendingRunToDelete = DeletedRunSyncEntity(
            runId = runId,
            userId = userId
        )
        runPendingSyncDao.upsertDeletedRunSyncEntity(pendingRunToDelete)

        val workRequest = OneTimeWorkRequestBuilder<DeleteRunWorker>()
            .addTag(DELETE_RUN_WORKER_TAG)
            .setConstraints(getConstraints())
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = BACK_OFF_DELAY_MILLIS,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(DeleteRunWorker.RUN_ID, runId)
                    .build()
            )
            .build()

        applicationScope.async {
            workManager.enqueue(workRequest).await()
        }.await()
    }

    private suspend fun scheduleDeleteRunsFromRemoteDb() {
        val newId = UUID.randomUUID()
        this.deleteAllRunsId = newId

        val workRequest = OneTimeWorkRequestBuilder<DeleteRunsFromRemoteDbWorker>()
            .setId(newId)
            .setConstraints(getConstraints())
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = BACK_OFF_DELAY_MILLIS,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .build()

        applicationScope.async {
            workManager.beginUniqueWork(
                DELETE_ALL_RUNS_UNIQUE_WORK,
                ExistingWorkPolicy.KEEP,
                workRequest
            ).enqueue().await()
        }.await()
    }

    private fun getConstraints(
        networkType: NetworkType = NetworkType.CONNECTED,
        requiresCharging: Boolean = false,
        requiresStorageNotLow: Boolean = false,
        requiresDeviceIdle: Boolean = false
    ): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(networkType)
            .setRequiresCharging(requiresCharging)
            .setRequiresStorageNotLow(requiresStorageNotLow)
            .setRequiresDeviceIdle(requiresDeviceIdle)
            .build()
    }

    override fun getWorkInformationForDeleteAllRuns(): Flow<WorkInformation?> {
        if (deleteAllRunsId == null) {
            return flowOf()
        }

        return workManager.getWorkInfosForUniqueWorkFlow(DELETE_ALL_RUNS_UNIQUE_WORK).map { workInfoList ->
            workInfoList.firstOrNull { it.id == deleteAllRunsId }?.toWorkInformation()
        }
    }

    override suspend fun cancelAllSyncs() {
        WorkManager.getInstance(context).cancelAllWork().await()
    }

    companion object {
        private const val FETCH_RUNS_WORKER_TAG = "fetch_runs_worker"
        private const val CREATE_RUN_WORKER_TAG = "create_run_worker"
        private const val DELETE_RUN_WORKER_TAG = "delete_run_worker"

        private const val BACK_OFF_DELAY_MILLIS = 2000L
    }
}