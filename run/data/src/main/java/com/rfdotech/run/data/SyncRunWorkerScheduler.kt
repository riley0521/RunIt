package com.rfdotech.run.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.await
import com.rfdotech.core.database.dao.RunPendingSyncDao
import com.rfdotech.core.database.entity.DeletedRunSyncEntity
import com.rfdotech.core.database.entity.RunPendingSyncEntity
import com.rfdotech.core.database.mapper.toRunEntity
import com.rfdotech.core.domain.auth.UserStorage
import com.rfdotech.core.domain.run.FETCH_RUNS_INTERVAL
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.run.SyncRunScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler.SyncType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerScheduler(
    private val context: Context,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val userStorage: UserStorage,
    private val applicationScope: CoroutineScope
) : SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(type: SyncType) {
        when (type) {
            is SyncType.CreateRun -> scheduleCreateRunWorker(type.run, type.mapPictureBytes)
            is SyncType.DeleteRun -> scheduleDeleteRunWorker(type.runId)
            is SyncType.FetchRuns -> scheduleFetchRunsWorker(type.interval)
        }
    }

    @SuppressLint("NewApi") // We have desugaring enabled so we are safe to use Duration and Dates
    private suspend fun scheduleFetchRunsWorker(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO) {
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
            .getConstraintForNetworkType()
            .getDefaultBackoffCriteria()
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
            .getConstraintForNetworkType()
            .getDefaultBackoffCriteria()
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
            .getConstraintForNetworkType()
            .getDefaultBackoffCriteria()
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

    private fun <B : WorkRequest.Builder<B, *>, W : WorkRequest> WorkRequest.Builder<B, W>.getDefaultBackoffCriteria(): WorkRequest.Builder<B, *> {
        return this.setBackoffCriteria(
            backoffPolicy = BackoffPolicy.EXPONENTIAL,
            backoffDelay = BACK_OFF_DELAY_MILLIS,
            timeUnit = TimeUnit.MILLISECONDS
        )
    }

    private fun <B : WorkRequest.Builder<B, *>, W : WorkRequest> WorkRequest.Builder<B, W>.getConstraintForNetworkType(
        type: NetworkType = NetworkType.CONNECTED
    ): WorkRequest.Builder<B, *> {
        return this.setConstraints(
            Constraints.Builder().setRequiredNetworkType(type).build()
        )
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