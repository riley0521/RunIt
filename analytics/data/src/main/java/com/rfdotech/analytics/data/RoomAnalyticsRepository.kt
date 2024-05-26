package com.rfdotech.analytics.data

import com.rfdotech.analytics.domain.AnalyticsValues
import com.rfdotech.analytics.domain.AnalyticsRepository
import com.rfdotech.analytics.domain.DateParam
import com.rfdotech.analytics.domain.toZonedDateTime
import com.rfdotech.core.database.dao.AnalyticsDao
import com.rfdotech.core.database.mapper.toRun
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.run.Run
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class RoomAnalyticsRepository(
    private val analyticsDao: AnalyticsDao,
    private val dispatcherProvider: DispatcherProvider
) : AnalyticsRepository {

    override suspend fun getAnalyticsValues(): AnalyticsValues {
        return withContext(dispatcherProvider.io) {
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

    override suspend fun getAllRunsThisMonth(): List<Run> {
        return analyticsDao.getAllRunsThisMonth().map { it.toRun() }
    }

    override suspend fun getAllRunsBetweenDates(
        startDate: DateParam,
        endDate: DateParam
    ): List<Run> {
        val startDateZoned = startDate.toZonedDateTime()
        val endDateZoned = endDate.toZonedDateTime(isEnd = true)

        return analyticsDao.getAllRunsBetweenDates(startDateZoned, endDateZoned).map { it.toRun() }
    }
}