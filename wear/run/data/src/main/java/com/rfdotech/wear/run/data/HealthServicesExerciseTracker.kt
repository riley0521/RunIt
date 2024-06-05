package com.rfdotech.wear.run.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesException
import androidx.health.services.client.clearUpdateCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseTrackedStatus
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import com.rfdotech.wear.run.domain.ExerciseError
import com.rfdotech.wear.run.domain.ExerciseTracker
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@Keep
class HealthServicesExerciseTracker(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) : ExerciseTracker {

    private val client = HealthServices.getClient(context).exerciseClient

    override val heartRate: Flow<Int>
        get() = callbackFlow {
            val callback = object : ExerciseUpdateCallback {
                override fun onAvailabilityChanged(
                    dataType: DataType<*, *>,
                    availability: Availability
                ) = Unit

                override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                    val heartRates = update.latestMetrics.getData(DataType.HEART_RATE_BPM)
                    val currentHeartRate = heartRates.firstOrNull()?.value

                    currentHeartRate?.let {
                        trySend(it.roundToInt())
                    }
                }

                override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) = Unit

                override fun onRegistered() = Unit

                override fun onRegistrationFailed(throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }

            client.setUpdateCallback(callback)

            awaitClose {
                runBlocking {
                    client.clearUpdateCallback(callback)
                }
            }
        }.flowOn(dispatcherProvider.io)

    override suspend fun isHeartRateTrackingSupported(): Boolean {
        return hasBodySensorsPermission() && runCatching {
            val capabilities = client.getCapabilities()
            val supportedDataTypes = capabilities
                .typeToCapabilities[ExerciseType.RUNNING]
                ?.supportedDataTypes ?: setOf()

            DataType.HEART_RATE_BPM in supportedDataTypes
        }.getOrDefault(false)
    }

    private suspend fun checkPermissionAndExerciseInfo(
        onSuccess: suspend () -> EmptyResult<ExerciseError>,
        checkIfOtherExerciseIsOngoing: Boolean = false
    ): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val result = getActiveExerciseInfo()
        val isOtherExerciseOngoing = if (checkIfOtherExerciseIsOngoing) {
            (result as? Result.Error)?.error == ExerciseError.ONGOING_OTHER_EXERCISE
        } else {
            true
        }

        if (result is Result.Error && isOtherExerciseOngoing) {
            return result
        }

        return onSuccess()
    }

    override suspend fun prepareExercise(): EmptyResult<ExerciseError> {
        return checkPermissionAndExerciseInfo(
            onSuccess = {
                val config = WarmUpConfig(
                    exerciseType = ExerciseType.RUNNING,
                    dataTypes = setOf(DataType.HEART_RATE_BPM)
                )
                client.prepareExercise(config)
                Result.Success(Unit)
            }
        )
    }

    override suspend fun startExercise(): EmptyResult<ExerciseError> {
        return checkPermissionAndExerciseInfo(
            onSuccess = {
                val config = ExerciseConfig.builder(ExerciseType.RUNNING)
                    .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                    .setIsAutoPauseAndResumeEnabled(false)
                    .build()

                client.startExercise(config)
                Result.Success(Unit)
            }
        )
    }

    override suspend fun resumeExercise(): EmptyResult<ExerciseError> {
        return checkPermissionAndExerciseInfo(
            onSuccess = {
                return@checkPermissionAndExerciseInfo try {
                    client.resumeExercise()
                    Result.Success(Unit)
                } catch (e: HealthServicesException) {
                    Result.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
                }
            },
            checkIfOtherExerciseIsOngoing = true
        )
    }

    override suspend fun pauseExercise(): EmptyResult<ExerciseError> {
        return checkPermissionAndExerciseInfo(
            onSuccess = {
                return@checkPermissionAndExerciseInfo try {
                    client.pauseExercise()
                    Result.Success(Unit)
                } catch (e: HealthServicesException) {
                    Result.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
                }
            },
            checkIfOtherExerciseIsOngoing = true
        )
    }

    override suspend fun stopExercise(): EmptyResult<ExerciseError> {
        return checkPermissionAndExerciseInfo(
            onSuccess = {
                return@checkPermissionAndExerciseInfo try {
                    client.endExercise()
                    Result.Success(Unit)
                } catch (e: HealthServicesException) {
                    Result.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
                }
            },
            checkIfOtherExerciseIsOngoing = true
        )
    }

    @SuppressLint("RestrictedApi")
    private suspend fun getActiveExerciseInfo(): EmptyResult<ExerciseError> {
        val info = client.getCurrentExerciseInfo()
        return when (info.exerciseTrackedStatus) {
            ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS -> Result.Success(Unit)
            ExerciseTrackedStatus.OWNED_EXERCISE_IN_PROGRESS -> {
                Result.Error(ExerciseError.ONGOING_OWN_EXERCISE)
            }
            ExerciseTrackedStatus.OTHER_APP_IN_PROGRESS -> {
                Result.Error(ExerciseError.ONGOING_OTHER_EXERCISE)
            }
            else -> {
                Result.Error(ExerciseError.UNKNOWN)
            }
        }
    }

    private fun hasBodySensorsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED
    }
}