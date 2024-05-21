package com.rfdotech.core.test_util.run

import com.rfdotech.core.domain.auth.UserId
import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result

class FakeRemoteRunDataSource : RemoteRunDataSource {

    private val runsMap = mutableMapOf<UserId, MutableList<Run>>(
        FAKE_USER_ID to mutableListOf()
    )
    var error: Boolean = false

    override suspend fun getAll(userId: String): Result<List<Run>, DataError.Network> {
        return if (error) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else {
            Result.Success(runsMap[userId].orEmpty())
        }
    }

    override suspend fun upsert(
        userId: String,
        run: Run,
        mapPicture: ByteArray
    ): Result<Run, DataError.Network> {
        return if (error) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else {
            runsMap[userId]?.add(run)
            Result.Success(run.copy(mapPictureUrl = "something"))
        }
    }

    override suspend fun deleteById(id: String): EmptyResult<DataError.Network> {
        return if (error) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else {
            runsMap[FAKE_USER_ID]?.removeIf { it.id == id }
            Result.Success(Unit)
        }
    }

    companion object {
        const val FAKE_USER_ID = "abc"
    }
}