package com.rfdotech.analytics.presentation.detail

import com.rfdotech.core.domain.run.Run
import java.time.YearMonth
import java.time.ZonedDateTime

data class AnalyticsDetailState(
    val showDatePickerDialog: Boolean = false,
    val runs: List<Run> = emptyList(),
    val startDate: ZonedDateTime = ZonedDateTime.now(),
    val endDate: ZonedDateTime = getLastDayOfMonth(startDate),
    val isGettingRuns: Boolean = false
)

fun getLastDayOfMonth(startDate: ZonedDateTime): ZonedDateTime {
    val yearMonth = YearMonth.now()
    val daysOfCurrentMonth = yearMonth.lengthOfMonth().toLong()
    val difference = (daysOfCurrentMonth - startDate.dayOfMonth).coerceAtLeast(0L)

    return startDate.plusDays(difference)
}
