package com.rfdotech.core.data.run

import com.rfdotech.core.domain.run.LocalRunDataSource
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalRunDataSource : LocalRunDataSource {

    private val runsMap = mutableMapOf<RunId, Run>()
    var error: Boolean = false

    override fun getAll(): Flow<List<Run>> {
        return flowOf(
            runsMap.values.toList()
        )
    }

    override suspend fun upsert(run: Run): Result<RunId, DataError.Local> {
        return if (error) {
            Result.Error(DataError.Local.DISK_FULL)
        } else {
            val runId = run.id.orEmpty()

            runsMap[runId] = run
            Result.Success(runId)
        }
    }

    override suspend fun upsertMultiple(runs: List<Run>): Result<List<RunId>, DataError.Local> {
        return if (error) {
            Result.Error(DataError.Local.DISK_FULL)
        } else {
            runs.map { run ->
                runsMap[run.id.orEmpty()] = run
            }
            Result.Success(runs.map { it.id.orEmpty() })
        }
    }

    override suspend fun deleteById(id: String) {
        runsMap.remove(id)
    }

    override suspend fun deleteAll() {
        runsMap.clear()
    }
}