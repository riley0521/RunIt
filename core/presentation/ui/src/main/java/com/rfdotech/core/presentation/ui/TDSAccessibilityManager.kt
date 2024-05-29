package com.rfdotech.core.presentation.ui

import android.content.Context
import androidx.annotation.PluralsRes
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * T - Time
 * D - Distance
 * S - Speed
 */

/**
 *
 */
fun Context.appendTextToKilometer(text: String, distanceKm: Double): String {
    val distanceKmStr = if (distanceKm > 1.0) {
        this.getString(R.string.x_km_plural, distanceKm.roundToDecimals().toString())
    } else {
        this.getString(R.string.x_km_singular, distanceKm.roundToDecimals().toString())
    }
    return "$text . $distanceKmStr"
}

fun Context.appendTextToKilometerPerHour(text: String, distanceKm: Double): String {
    val distanceKmStr = if (distanceKm > 1.0) {
        this.getString(R.string.x_km_per_hour_plural, distanceKm.roundToDecimals().toString())
    } else {
        this.getString(R.string.x_km_per_hour_singular, distanceKm.roundToDecimals().toString())
    }

    return "$text . $distanceKmStr"
}

fun Context.appendTextToDayHourMinute(text: String, time: Duration): String {
    val dayStr = this.getPlurals(R.plurals.x_day, time.getInt(DurationUnit.DAYS))
    val hourStr = this.getPlurals(R.plurals.x_hour, time.getRemainingHours())
    val minuteStr = this.getPlurals(R.plurals.x_minute, time.getRemainingMinutes())

    return "$text . $dayStr $hourStr $minuteStr"
}

fun Context.appendTextToHourMinuteSecond(text: String, time: Duration): String {
    val hourStr = this.getPlurals(R.plurals.x_hour, time.getInt(DurationUnit.HOURS))
    val minuteStr = this.getPlurals(R.plurals.x_minute, time.getRemainingMinutes())
    val secondStr = this.getPlurals(R.plurals.x_second, time.getRemainingSeconds())

    return "$text . $hourStr $minuteStr $secondStr"
}

fun Context.appendTextToPace(text: String, distanceKm: Double, time: Duration): String {
    val averagePace = DistanceAndSpeedCalculator.getAveragePacePerKilometer(distanceKm, time) ?: return ""

    val formattedMinute = this.getPlurals(R.plurals.x_minute, averagePace.minutes)
    val formattedSeconds = this.getPlurals(R.plurals.x_second, averagePace.seconds)

    val formatted = this.getString(R.string.x_pace_acc, "$formattedMinute , $formattedSeconds")

    return "$text . $formatted"
}

private fun Context.getPlurals(@PluralsRes id: Int, quantity: Int): String {
    if (quantity == 0) {
        return ""
    }

    return this.resources.getQuantityString(id, quantity, quantity)
}

private const val EN_DEFAULT_FULL_DATE_PATTERN = "MMM dd, yyyy"
private const val FR_DEFAULT_FULL_DATE_PATTERN = "dd MMM yyyy"
private const val EN_DATE_TIME_PATTERN = "MMM dd, yyyy - hh:mma"
private const val FR_DATE_TIME_PATTERN = "dd MMM yyyy - HH:mm"
private const val EN_MONTH_DAY_DATE_PATTERN = "MMM d"
private const val FR_MONTH_DAY_DATE_PATTERN = "d MMM"

fun Locale.getMonthDayPattern(): String {
    return when (this) {
        Locale.ENGLISH -> EN_MONTH_DAY_DATE_PATTERN
        Locale.FRENCH -> FR_MONTH_DAY_DATE_PATTERN
        else -> EN_MONTH_DAY_DATE_PATTERN
    }
}

fun Locale.getFullDatePattern(): String {
    return when (this) {
        Locale.ENGLISH -> EN_DEFAULT_FULL_DATE_PATTERN
        Locale.FRENCH -> FR_DEFAULT_FULL_DATE_PATTERN
        else -> EN_DEFAULT_FULL_DATE_PATTERN
    }
}

fun Locale.getDateTimePattern(): String {
    return when (this) {
        Locale.ENGLISH -> EN_DATE_TIME_PATTERN
        Locale.FRENCH -> FR_DATE_TIME_PATTERN
        else -> EN_DATE_TIME_PATTERN
    }
}

fun Context.getTextForDateRange(startDate: LocalDate, endDate: LocalDate): String {
    val pattern = Locale.getDefault().getFullDatePattern()
    val formatter = DateTimeFormatter.ofPattern(pattern)

    return this.getString(R.string.start_date_end_date, formatter.format(startDate), formatter.format(endDate))
}

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