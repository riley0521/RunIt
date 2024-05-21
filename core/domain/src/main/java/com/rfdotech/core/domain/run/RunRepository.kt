package com.rfdotech.core.domain.run

import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    fun getAllLocal(): Flow<List<Run>>
    suspend fun fetchFromRemote(): EmptyResult<DataError>
    suspend fun upsert(run: Run, mapPicture: ByteArray): EmptyResult<DataError>
    suspend fun deleteById(id: RunId)
    suspend fun deleteAllFromLocal()
    suspend fun syncPendingRuns()

    @Deprecated("We do not use this on our current set up with Firebase Authentication.")
    suspend fun signOut(): EmptyResult<DataError.Network>
}