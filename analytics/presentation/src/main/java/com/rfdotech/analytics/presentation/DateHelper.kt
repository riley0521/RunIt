package com.rfdotech.analytics.presentation

import com.rfdotech.analytics.domain.DateParam
import com.rfdotech.core.presentation.ui.getFullDatePattern
import com.rfdotech.core.presentation.ui.getMonthYearPattern
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateHelper {

    fun getMonthAndYearFormatted(locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern(locale.getMonthYearPattern())
        val now = ZonedDateTime.now()

        return formatter.format(now)
    }

    fun getFormattedDate(startDate: LocalDate, endDate: LocalDate, locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern(locale.getFullDatePattern())

        return "${formatter.format(startDate)} - ${formatter.format(endDate)}"
    }

    fun convertLocalDateToDateParam(date: LocalDate): DateParam {
        return DateParam(
            year = date.year,
            month = date.monthValue,
            day = date.dayOfMonth
        )
    }

    fun getAllowedDates(): ClosedRange<LocalDate> {
        val range = LocalDate.of(2000, 1, 1) .. LocalDate.now()
        return range
    }
}