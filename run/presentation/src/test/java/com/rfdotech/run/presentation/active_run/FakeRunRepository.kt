package com.rfdotech.run.presentation.active_run

import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import com.rfdotech.run.presentation.run
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

val FAKE_RUNS = listOf(
    run(id = "111"),
    run(id = "112", maxSpeedKmh = 10.5)
)

class FakeRunRepository : RunRepository {

    private val runsFlow = MutableStateFlow<List<Run>>(emptyList())
    var error: Boolean = false

    fun isNotEmpty(): Boolean {
        return runsFlow.value.isNotEmpty()
    }

    override fun getAllLocal(): Flow<List<Run>> {
        return runsFlow
    }

    override suspend fun fetchFromRemote(): EmptyResult<DataError> {
        runsFlow.update { it + FAKE_RUNS }
        return if (error) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else Result.Success(Unit)
    }

    override suspend fun upsert(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        runsFlow.update { it + run }
        return if (error) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else Result.Success(Unit)
    }

    override suspend fun deleteById(id: RunId) {
        runsFlow.update { runs ->
            runs.filter { it.id != id }
        }
    }

    override suspend fun deleteAllFromLocal() {
        runsFlow.update { emptyList() }
    }

    override suspend fun syncPendingRuns() {
        // no-op
    }

    override suspend fun signOut(): EmptyResult<DataError.Network> {
        TODO("Not yet implemented")
    }
}