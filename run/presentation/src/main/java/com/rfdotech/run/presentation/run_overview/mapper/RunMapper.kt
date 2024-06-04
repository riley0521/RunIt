package com.rfdotech.run.presentation.run_overview.mapper

import android.content.Context
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import com.rfdotech.core.domain.run.RunWithAddress
import com.rfdotech.core.presentation.ui.formatted
import com.rfdotech.core.presentation.ui.getDateTimePattern
import com.rfdotech.core.presentation.ui.toFormattedHeartRate
import com.rfdotech.core.presentation.ui.toFormattedKm
import com.rfdotech.core.presentation.ui.toFormattedKmh
import com.rfdotech.core.presentation.ui.toFormattedMeters
import com.rfdotech.core.presentation.ui.toFormattedPace
import com.rfdotech.core.presentation.ui.toFormattedSteps
import com.rfdotech.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun RunWithAddress.toRunUi(context: Context): RunUi = with(run) {
    val dateTimeInLocalTime = dateTimeUtc.withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter
        .ofPattern(Locale.getDefault().getDateTimePattern())
        .format(dateTimeInLocalTime)

    val distanceKm = DistanceAndSpeedCalculator.getKmFromMeters(distanceMeters)

    return RunUi(
        id = id.orEmpty(),
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKm(context),
        avgSpeed = avgSpeedKhm.toFormattedKmh(context),
        maxSpeed = maxSpeedKmh.toFormattedKmh(context),
        pace = duration.toFormattedPace(distanceKm, context),
        totalElevation = totalElevationMeters.toFormattedMeters(context),
        numberOfSteps = numberOfSteps.toFormattedSteps(context),
        avgHeartRate = avgHeartRate.toFormattedHeartRate(context),
        mapPictureUrl = mapPictureUrl,
        address = address
    )
}