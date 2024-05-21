package com.rfdotech.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rfdotech.core.database.dao.RunPendingSyncDao
import com.rfdotech.core.database.mapper.toRun
import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.core.domain.util.Result as MyResult

class CreateRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val runPendingSyncDao: RunPendingSyncDao
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= Constants.MAX_RETRY_COUNT_WORKER) {
            return Result.failure()
        }

        val userId = params.inputData.getString(USER_ID) ?: return Result.failure()
        val pendingRunId = params.inputData.getString(RUN_ID) ?: return Result.failure()
        val pendingRunEntity = runPendingSyncDao.getRunPendingSyncEntity(pendingRunId)
            ?: return Result.failure()

        val run = pendingRunEntity.run.toRun()
        return when (val result = remoteRunDataSource.upsert(userId, run, pendingRunEntity.mapPictureBytes)) {
            is MyResult.Error -> result.error.toWorkerResult()
            is MyResult.Success -> {
                runPendingSyncDao.deleteRunPendingSyncEntity(pendingRunId)
                Result.success()
            }
        }
    }

    companion object {
        const val RUN_ID = "RUN_ID"
        const val USER_ID = "USER_ID"
    }
}