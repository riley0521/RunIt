package com.rfdotech.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rfdotech.core.database.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Upsert
    suspend fun upsert(run: RunEntity)

    @Upsert
    suspend fun upsertMultiple(runs: List<RunEntity>)

    @Query("SELECT * FROM tbl_runs ORDER BY dateTimeUtc DESC")
    fun getAll(): Flow<List<RunEntity>>

    @Query("DELETE FROM tbl_runs WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM tbl_runs")
    suspend fun deleteAll()
}