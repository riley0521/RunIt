package com.rfdotech.core.domain.run

import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    fun getAllLocal(): Flow<List<Run>>
    suspend fun fetchFromRemote(): EmptyResult<DataError>
    suspend fun upsert(run: Run, mapPicture: ByteArray): EmptyResult<DataError>
    suspend fun deleteById(id: RunId)
}