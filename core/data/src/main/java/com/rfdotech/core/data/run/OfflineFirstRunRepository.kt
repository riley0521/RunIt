package com.rfdotech.core.data.run

import com.rfdotech.core.data.networking.get
import com.rfdotech.core.database.dao.RunPendingSyncDao
import com.rfdotech.core.database.mapper.toRun
import com.rfdotech.core.domain.auth.UserStorage
import com.rfdotech.core.domain.run.LocalRunDataSource
import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.run.SyncRunScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler.SyncType
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val userStorage: UserStorage,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val syncRunScheduler: SyncRunScheduler,
    private val httpClient: HttpClient,
    private val applicationScope: CoroutineScope
) : RunRepository {
    override fun getAllLocal(): Flow<List<Run>> {
        return localRunDataSource.getAll()
    }

    override suspend fun fetchFromRemote(): EmptyResult<DataError> {
        val userId = getUserId() ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return when (val result = remoteRunDataSource.getAll(userId)) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertMultiple(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsert(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsert(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }

        val userId = getUserId() ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.upsert(
            userId,
            run = runWithId,
            mapPicture = mapPicture
        )

        return when (remoteResult) {
            is Result.Error -> {
                applicationScope.async {
                    syncRunScheduler.scheduleSync(SyncType.CreateRun(runWithId, mapPicture))
                    Result.Success(Unit)
                }.await()
            }
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsert(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteAllFromLocal() {
        localRunDataSource.deleteAll()
    }

    override suspend fun deleteById(id: RunId) {
        localRunDataSource.deleteById(id)

        // Edge case where the run is created offline. We make sure that it gets deleted in run pending sync and simply return.
        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(id) != null
        if (isPendingSync) {
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteById(id)
        }.await()

        if (remoteResult is Result.Error) {
            applicationScope.async {
                syncRunScheduler.scheduleSync(SyncType.DeleteRun(id))
            }.await()
        }
    }

    private suspend fun getUserId(): String? {
        return userStorage.get()
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = getUserId() ?: return@withContext

            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId)
            }

            val createJobs = createdRuns
                .await()
                .map {
                    launch {
                        val run = it.run.toRun()
                        when (remoteRunDataSource.upsert(userId, run, it.mapPictureBytes)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteRunPendingSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            val deleteJobs = deletedRuns
                .await()
                .map {
                    launch {
                        when (remoteRunDataSource.deleteById(it.runId)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteDeletedRunSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            createJobs.forEach { it.join() }
            deleteJobs.forEach { it.join() }
        }
    }

    @Deprecated("We do not use this on our current set up with Firebase Authentication.")
    override suspend fun signOut(): EmptyResult<DataError.Network> {
        val result = httpClient.get<Unit>(
            route = "/logout"
        ).asEmptyDataResult()

        httpClient.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
            .firstOrNull()
            ?.clearToken()

        return result
    }
}