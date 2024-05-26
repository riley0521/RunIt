package com.rfdotech.analytics.domain

import com.rfdotech.core.domain.run.Run

interface AnalyticsRepository {
    suspend fun getAnalyticsValues(): AnalyticsValues
    suspend fun getAllRunsThisMonth(): List<Run>
    suspend fun getAllRunsBetweenDates(startDate: DateParam, endDate: DateParam): List<Run>
}