package com.rfdotech.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.Result as MyResult

class FetchRunsWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= Constants.MAX_RETRY_COUNT_WORKER) {
            return Result.failure()
        }

        return when (val result = runRepository.fetchFromRemote()) {
            is MyResult.Error -> result.error.toWorkerResult()
            is MyResult.Success -> Result.success()
        }
    }
}