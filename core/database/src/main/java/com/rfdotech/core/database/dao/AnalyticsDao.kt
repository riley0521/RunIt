package com.rfdotech.core.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AnalyticsDao {

    @Query("SELECT SUM(distanceMeters) FROM tbl_runs")
    suspend fun getTotalDistance(): Int

    @Query("SELECT SUM(durationMillis) FROM tbl_runs")
    suspend fun getTotalTimeRun(): Long

    @Query("SELECT MAX(maxSpeedKmh) FROM tbl_runs")
    suspend fun getMaxRunSpeed(): Double

    @Query("SELECT AVG(distanceMeters) FROM tbl_runs")
    suspend fun getAvgDistancePerRun(): Double

    /**
     * @return Average pace per run PER kilometers
     */
    @Query("SELECT AVG((durationMillis / 60000.0) / (distanceMeters / 1000.0)) FROM tbl_runs")
    suspend fun getAvgPacePerRun(): Double
}