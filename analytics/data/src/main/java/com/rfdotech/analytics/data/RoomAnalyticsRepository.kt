package com.rfdotech.analytics.data

import com.rfdotech.analytics.domain.AnalyticsValues
import com.rfdotech.analytics.domain.AnalyticsRepository
import com.rfdotech.core.database.dao.AnalyticsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class RoomAnalyticsRepository(
    private val analyticsDao: AnalyticsDao
) : AnalyticsRepository {

    override suspend fun getAnalyticsValues(): AnalyticsValues {
        return withContext(Dispatchers.IO) {
            val totalDistanceRun = async { analyticsDao.getTotalDistance() }
            val totalTimeRun = async { analyticsDao.getTotalTimeRun().milliseconds }
            val fastestEverRun = async { analyticsDao.getMaxRunSpeed() }
            val avgDistancePerRun = async { analyticsDao.getAvgDistancePerRun() }
            val avgPacePerRun = async { analyticsDao.getAvgPacePerRun() }

            AnalyticsValues(
                totalDistanceMeters = totalDistanceRun.await(),
                totalTimeRun = totalTimeRun.await(),
                fastestEverRun = fastestEverRun.await(),
                avgDistanceMeters = avgDistancePerRun.await(),
                avgPacePerRun = avgPacePerRun.await()
            )
        }
    }


}