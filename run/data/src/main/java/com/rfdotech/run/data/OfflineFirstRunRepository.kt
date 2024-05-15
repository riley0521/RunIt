package com.rfdotech.run.data

import com.rfdotech.core.database.mapper.toRunEntity
import com.rfdotech.core.domain.run.LocalRunDataSource
import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope
) : RunRepository {
    override fun getAllLocal(): Flow<List<Run>> {
        return localRunDataSource.getAll()
    }

    override suspend fun fetchFromRemote(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getAll()) {
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

        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.upsert(
            run = runWithId,
            mapPicture = mapPicture
        )

        return when (remoteResult) {
            is Result.Error -> {
                Result.Error(DataError.Network.NO_INTERNET)
            }
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsert(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteById(id: RunId) {
        localRunDataSource.deleteById(id)

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteById(id)
        }

        return when (remoteResult.await()) {
            is Result.Error -> TODO()
            is Result.Success -> TODO()
        }
    }
}