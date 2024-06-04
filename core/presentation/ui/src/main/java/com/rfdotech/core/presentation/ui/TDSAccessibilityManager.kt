package com.rfdotech.core.presentation.ui

import android.content.Context
import androidx.annotation.PluralsRes
import com.rfdotech.core.domain.getInt
import com.rfdotech.core.domain.getRemainingHours
import com.rfdotech.core.domain.getRemainingMinutes
import com.rfdotech.core.domain.getRemainingSeconds
import com.rfdotech.core.domain.roundToDecimals
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * You can write everything here related to formatting date & time, distance or speed.
 *
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

fun Context.appendTextToHeartRate(text: String, bpm: Int?): String {
    return "$text . ${this.getPlurals(R.plurals.x_heart_rate_acc, bpm ?: 0)}"
}

private fun Context.getPlurals(@PluralsRes id: Int, quantity: Int): String {
    if (quantity == 0) {
        return ""
    }

    return this.resources.getQuantityString(id, quantity, quantity)
}

// Full Date
private const val EN_DEFAULT_FULL_DATE_PATTERN = "MMM dd, yyyy"
private const val FR_DEFAULT_FULL_DATE_PATTERN = "dd MMM yyyy"

// Date Time
private const val EN_DATE_TIME_PATTERN = "MMM dd, yyyy - hh:mma"
private const val FR_DATE_TIME_PATTERN = "dd MMM yyyy - HH:mm"

// Month Day
private const val EN_MONTH_DAY_DATE_PATTERN = "MMM d"
private const val FR_MONTH_DAY_DATE_PATTERN = "d MMM"

// Month Year
private const val EN_MONTH_YEAR_DATE_PATTERN = "MMM yyyy"

fun Locale.getMonthDayPattern(): String {
    return when (this) {
        Locale.ENGLISH -> EN_MONTH_DAY_DATE_PATTERN
        Locale.FRENCH -> FR_MONTH_DAY_DATE_PATTERN
        else -> EN_MONTH_DAY_DATE_PATTERN
    }
}

@Suppress("SameReturnValue") // Will add more supported language in the future
fun Locale.getMonthYearPattern(): String {
    return when (this) {
        Locale.ENGLISH -> EN_MONTH_YEAR_DATE_PATTERN
        else -> EN_MONTH_YEAR_DATE_PATTERN
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

fun Context.getTextForDateRange(startDate: LocalDate, endDate: LocalDate, locale: Locale = Locale.getDefault()): String {
    val pattern = locale.getFullDatePattern()
    val formatter = DateTimeFormatter.ofPattern(pattern)

    return this.getString(R.string.start_date_end_date, formatter.format(startDate), formatter.format(endDate))
}

fun Duration.formatted(): String {
    val hours = this.getInt(DurationUnit.HOURS).formatNumberWithLeadingZero()
    val minutes = this.getRemainingMinutes().formatNumberWithLeadingZero()
    val seconds = this.getRemainingSeconds().formatNumberWithLeadingZero()

    return "$hours:$minutes:$seconds"
}

fun Int.formatNumberWithLeadingZero(): String {
    return String.format(Locale.getDefault(), "%02d", this)
}

fun Double.toFormattedKm(context: Context): String {
    return context.getString(R.string.x_km, this.roundToDecimals().toString())
}

fun Double.toFormattedKmh(context: Context): String {
    return context.getString(R.string.x_km_per_hour, this.roundToDecimals().toString())
}

fun Int.toFormattedMeters(context: Context): String {
    return context.getString(R.string.x_meter, this)
}

fun Duration.toFormattedPace(distanceKm: Double, context: Context): String {
    if (this == Duration.ZERO || distanceKm <= 0.0) {
        return "-"
    }

    val averagePace = DistanceAndSpeedCalculator.getAveragePacePerKilometer(
        distanceKm,
        this
    ) ?: return ""

    return context.getString(
        R.string.x_pace,
        "${averagePace.minutes}:${averagePace.seconds.formatNumberWithLeadingZero()}"
    )
}

fun Int.toFormattedSteps(context: Context): String {
    return if (this > 0) {
        context.getPlurals(R.plurals.x_num_of_steps, this)
    } else {
        "-"
    }
}

fun Int?.toFormattedHeartRate(context: Context): String {
    val heartRate = this ?: 0
    return if (heartRate > 0) {
        context.getString(R.string.x_heart_rate, this)
    } else {
        "-"
    }
}