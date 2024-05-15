package com.rfdotech.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rfdotech.core.database.dao.RunPendingSyncDao
import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.util.Result as MyResult

class DeleteRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val runPendingSyncDao: RunPendingSyncDao
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= Constants.MAX_RETRY_COUNT_WORKER) {
            return Result.failure()
        }

        val runId = params.inputData.getString(RUN_ID) ?: return Result.failure()
        return when (val result = remoteRunDataSource.deleteById(runId)) {
            is MyResult.Error -> result.error.toWorkerResult()
            is MyResult.Success -> {
                runPendingSyncDao.deleteDeletedRunSyncEntity(runId)
                Result.success()
            }
        }
    }

    companion object {
        const val RUN_ID = "RUN_ID"
    }
}