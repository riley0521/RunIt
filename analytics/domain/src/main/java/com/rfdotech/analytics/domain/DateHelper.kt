package com.rfdotech.analytics.domain

import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateHelper {

    fun getMontAndYearFormatted(): String {
        val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
        val now = ZonedDateTime.now()

        return formatter.format(now)
    }

    fun getFormattedDate(startDate: LocalDate, endDate: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

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