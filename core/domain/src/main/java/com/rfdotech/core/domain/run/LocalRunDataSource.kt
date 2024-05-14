package com.rfdotech.core.domain.run

import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

typealias RunId = String

interface LocalRunDataSource {
    fun getAll(): Flow<List<Run>>
    suspend fun upsert(run: Run): Result<RunId, DataError.Local>
    suspend fun upsertMultiple(runs: List<Run>): Result<List<RunId>, DataError.Local>
    suspend fun deleteById(id: String)
    suspend fun deleteAll()
}