package com.rfdotech.core.database.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeConverter {

    @TypeConverter
    fun zonedDateTimeToLong(value: ZonedDateTime): Long {
        return value.toInstant().toEpochMilli()
    }

    @TypeConverter
    fun longToZonedDateTime(value: Long): ZonedDateTime {
        return Instant.ofEpochMilli(value).atZone(ZoneId.of("UTC"))
    }
}