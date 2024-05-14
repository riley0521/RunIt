package com.rfdotech.core.database

import android.database.sqlite.SQLiteFullException
import com.rfdotech.core.database.dao.RunDao
import com.rfdotech.core.database.mapper.toRun
import com.rfdotech.core.database.mapper.toRunEntity
import com.rfdotech.core.domain.run.LocalRunDataSource
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalRunDataSource(
    private val runDao: RunDao
) : LocalRunDataSource {
    override fun getAll(): Flow<List<Run>> {
        return runDao.getAll().map { runs -> runs.map { it.toRun() } }
    }

    override suspend fun upsert(run: Run): Result<RunId, DataError.Local> {
        return try {
            val entity = run.toRunEntity()
            runDao.upsert(entity)

            Result.Success(entity.id)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun upsertMultiple(runs: List<Run>): Result<List<RunId>, DataError.Local> {
        return try {
            val runEntities = runs.map { it.toRunEntity() }
            val runIds = runEntities.map { it.id }
            runDao.upsertMultiple(runEntities)

            Result.Success(runIds)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteById(id: String) {
        runDao.deleteById(id)
    }

    override suspend fun deleteAll() {
        runDao.deleteAll()
    }
}