package com.rfdotech.core.domain

import kotlin.time.Duration
import kotlin.time.DurationUnit

const val HOURS_PER_DAY = 24
const val SECONDS_PER_MINUTE = 60

fun Duration.getInt(unit: DurationUnit): Int {
    return this.toLong(unit).toInt()
}

fun Duration.getRemainingHours(): Int {
    return this.getInt(DurationUnit.HOURS) % HOURS_PER_DAY
}

fun Duration.getRemainingMinutes(): Int {
    return this.getInt(DurationUnit.MINUTES) % SECONDS_PER_MINUTE
}

fun Duration.getRemainingSeconds(): Int {
    return this.getInt(DurationUnit.SECONDS) % SECONDS_PER_MINUTE
}