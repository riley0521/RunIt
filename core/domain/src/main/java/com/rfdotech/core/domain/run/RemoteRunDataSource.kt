package com.rfdotech.core.domain.run

import com.rfdotech.core.domain.auth.UserId
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result

interface RemoteRunDataSource {
    suspend fun getAll(userId: UserId): Result<List<Run>, DataError.Network>
    suspend fun upsert(userId: UserId, run: Run, mapPicture: ByteArray): Result<Run, DataError.Network>
    suspend fun deleteById(id: RunId): EmptyResult<DataError.Network>
}