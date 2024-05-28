package com.rfdotech.core.domain

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object ZonedDateTimeHelper {

    val UTC_ZONE = ZoneId.of("UTC")

    fun addZoneIdToZonedDateTime(date: ZonedDateTime, zoneId: ZoneId = UTC_ZONE): ZonedDateTime {
        return date.withZoneSameInstant(zoneId)
    }

    fun longToZonedDateTime(millis: Long, zoneId: ZoneId = UTC_ZONE): ZonedDateTime {
        return Instant.ofEpochMilli(millis).atZone(zoneId)
    }
}