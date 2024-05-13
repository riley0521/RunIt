package com.rfdotech.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.rfdotech.core.domain.location.LocationTimestamp
import kotlin.math.abs

object PolyLineColorCalculator {

    private const val MIN_SPEED = 5.0
    private const val MAX_SPEED = 20.0

    fun locationsToColor(
        location1: LocationTimestamp,
        location2: LocationTimestamp
    ): Color {
        val distanceMeters = location1.location.location.distanceTo(location2.location.location)
        val timeDiff = abs((location2.durationTimestamp - location1.durationTimestamp).inWholeSeconds)

        val speedKmh = (distanceMeters / timeDiff) * 3.6

        return interpolateColor(
            speedKmh = speedKmh,
            colorStart = Color.Green,
            colorMid = Color.Yellow,
            colorEnd = Color.Red
        )
    }

    private fun interpolateColor(
        speedKmh: Double,
        colorStart: Color,
        colorMid: Color,
        colorEnd: Color
    ): Color {
        val ratio = ((speedKmh - MIN_SPEED) / (MAX_SPEED - MIN_SPEED)).coerceIn(0.0..1.0)
        val colorInt = if (ratio <= 0.5) {
            val midRatio = ratio / 0.5
            ColorUtils.blendARGB(colorStart.toArgb(), colorMid.toArgb(), midRatio.toFloat())
        } else {
            val midToEndRatio = (ratio - 0.5) / 0.5
            ColorUtils.blendARGB(colorMid.toArgb(), colorEnd.toArgb(), midToEndRatio.toFloat())
        }

        return Color(colorInt)
    }
}