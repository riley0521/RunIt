package com.rfdotech.run.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.rfdotech.core.domain.auth.UserStorage
import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.core.domain.run.RunRepository

class DeleteRunsFromRemoteDbWorker(
    private val context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val userStorage: UserStorage
): CoroutineWorker(context, params) {

    private val notificationManager by lazy {
        context.getSystemService(NotificationManager::class.java)
    }

    private val baseNotification by lazy {
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(context.getString(R.string.channel_description))
    }

    override suspend fun doWork(): Result {
        if (runAttemptCount >= Constants.MAX_RETRY_COUNT_WORKER) {
            return Result.failure()
        }

        setForeground(createForegroundInfo())

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
                // TODO
                // WorkManager does not allow us to update the notification here because
                // Result.success() will immediately kill androidx.work.impl.foreground.SystemForegroundService
                // Need to think another way to let the user know that the account deletion is successful.
                Result.success()
            }
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        createChannel()

        val notification = baseNotification
            .setOngoing(true)
            .setContentText(context.getString(R.string.deleting_account))
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_description),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "delete_account"
        private const val NOTIFICATION_ID = 2
    }
}