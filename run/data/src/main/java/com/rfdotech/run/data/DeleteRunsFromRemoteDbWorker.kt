package com.rfdotech.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rfdotech.core.domain.auth.UserStorage
import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.core.domain.run.RunRepository

class DeleteRunsFromRemoteDbWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val userStorage: UserStorage
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= Constants.MAX_RETRY_COUNT_WORKER) {
            return Result.failure()
        }

        val userId = userStorage.get() ?: return Result.failure()
        return when (val result = runRepository.deleteAllFromRemote()) {
            is com.rfdotech.core.domain.util.Result.Error -> result.error.toWorkerResult()
            is com.rfdotech.core.domain.util.Result.Success -> {

                // Check if there are no more runs in the remote db.
                when (val runsResult = remoteRunDataSource.getAll(userId)) {
                    is com.rfdotech.core.domain.util.Result.Error -> runsResult.error.toWorkerResult()
                    is com.rfdotech.core.domain.util.Result.Success -> {
                        if (runsResult.data.isNotEmpty()) {
                            return Result.retry()
                        }
                    }
                }

                runRepository.deleteAllFromLocal()
                Result.success()
            }
        }
    }
}