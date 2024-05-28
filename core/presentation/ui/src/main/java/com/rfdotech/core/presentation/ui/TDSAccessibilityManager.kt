package com.rfdotech.core.presentation.ui

import android.content.Context
import androidx.annotation.PluralsRes
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
class TDSAccessibilityManager(
    private val context: Context
) {

    fun appendTextToKilometer(text: String, distanceKm: Double): String {
        val distanceKmStr = if (distanceKm > 1.0) {
            context.getString(R.string.x_km_plural, formatDouble(distanceKm))
        } else {
            context.getString(R.string.x_km_singular, formatDouble(distanceKm))
        }
        return "$text . $distanceKmStr"
    }

    fun appendTextToKilometerPerHour(text: String, distanceKm: Double): String {
        val distanceKmStr = if (distanceKm > 1.0) {
            context.getString(R.string.x_km_per_hour_plural, formatDouble(distanceKm))
        } else {
            context.getString(R.string.x_km_per_hour_singular, formatDouble(distanceKm))
        }

        return "$text . $distanceKmStr"
    }

    fun formatDouble(value: Double): String {
        return String.format(Locale.getDefault(), "%.1f", value)
    }

    fun appendTextToDayHourMinute(text: String, time: Duration): String {
        val dayStr = context.getPlurals(R.plurals.x_day, time.getInt(DurationUnit.DAYS))
        val hourStr = context.getPlurals(R.plurals.x_hour, time.getInt(DurationUnit.HOURS) % 24)
        val minuteStr = context.getPlurals(R.plurals.x_minute, time.getInt(DurationUnit.MINUTES) % 60)

        return "$text . $dayStr $hourStr $minuteStr"
    }

    private fun Duration.getInt(unit: DurationUnit): Int {
        return this.toLong(unit).toInt()
    }

    fun appendTextToHourMinuteSecond(text: String, time: Duration): String {
        val hourStr = context.getPlurals(R.plurals.x_hour, time.getInt(DurationUnit.HOURS))
        val minuteStr = context.getPlurals(R.plurals.x_minute, time.getInt(DurationUnit.MINUTES) % 60)
        val secondStr = context.getPlurals(R.plurals.x_second, time.getInt(DurationUnit.SECONDS) % 60)

        return "$text . $hourStr $minuteStr $secondStr"
    }

    private fun Context.getPlurals(@PluralsRes id: Int, quantity: Int): String {
        return this.resources.getQuantityString(id, quantity, quantity)
    }

    fun getTextForDateRange(startDate: LocalDate, endDate: LocalDate, datePattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(datePattern)

        return context.getString(R.string.start_date_end_date, formatter.format(startDate), formatter.format(endDate))
    }
}