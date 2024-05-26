package com.rfdotech.analytics.domain

import java.time.ZoneId
import java.time.ZonedDateTime

data class DateParam(
    val year: Int,
    val month: Int,
    val day: Int
)

fun DateParam.toZonedDateTime(isEnd: Boolean = false): ZonedDateTime {
    return if (isEnd) {
        ZonedDateTime.of(year, month, day, 23, 59, 59, 999_999_999, ZoneId.of("UTC"))
    } else {
        ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.of("UTC"))
    }
}
