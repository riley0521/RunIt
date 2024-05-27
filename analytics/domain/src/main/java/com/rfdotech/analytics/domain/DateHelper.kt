package com.rfdotech.analytics.domain

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateHelper {

    fun getMontAndYearFormatted(): String {
        val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
        val now = ZonedDateTime.now()

        return formatter.format(now)
    }

    fun getFormattedDate(startDate: ZonedDateTime, endDate: ZonedDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

        return "${formatter.format(startDate)} - ${formatter.format(endDate)}"
    }
}