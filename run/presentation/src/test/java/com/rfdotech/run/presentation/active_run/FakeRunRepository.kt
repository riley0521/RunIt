package com.rfdotech.run.presentation.active_run

import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.test_util.run.run
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

val FAKE_RUNS = listOf(
    run(id = "111"),
    run(id = "112", maxSpeedKmh = 10.5)
)

class FakeRunRepository : RunRepository {

    private val runsMap = mutableMapOf<RunId, Run>()
    private val runsMapRemote = mutableMapOf<RunId, Run>()
    var error: Boolean = false

    fun isNotEmpty(): Boolean {
        return runsMap.isNotEmpty()
    }

    override fun getAllLocal(): Flow<List<Run>> {
        return flowOf(
            runsMap.values.toList()
        )
    }

    override suspend fun fetchFromRemote(): EmptyResult<DataError> {
        return if (error) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else {
            FAKE_RUNS.map {
                runsMap[it.id.orEmpty()] = it
                runsMapRemote[it.id.orEmpty()] = it
            }
            Result.Success(Unit)
        }
    }

    override suspend fun upsert(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        return if (error) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else {
            runsMap[run.id.orEmpty()] = run
            runsMapRemote[run.id.orEmpty()] = run
            Result.Success(Unit)
        }
    }

    override suspend fun deleteById(id: RunId) {
        runsMap.remove(id)
        runsMapRemote.remove(id)
    }

    override suspend fun deleteAllFromLocal() {
        runsMap.clear()
    }

    override suspend fun syncPendingRuns() {
        // no-op
    }

    override suspend fun deleteAllFromRemote(): EmptyResult<DataError> {
        return if (error) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else {
            runsMapRemote.clear()
            Result.Success(Unit)
        }
    }

    @Deprecated("We do not use this on our current set up with Firebase Authentication.")
    override suspend fun signOut(): EmptyResult<DataError.Network> {
        TODO("Not yet implemented")
    }
}