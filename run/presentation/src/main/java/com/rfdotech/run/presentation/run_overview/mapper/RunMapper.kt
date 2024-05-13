package com.rfdotech.run.presentation.run_overview.mapper

import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.presentation.ui.formatted
import com.rfdotech.core.presentation.ui.toFormattedKm
import com.rfdotech.core.presentation.ui.toFormattedKmh
import com.rfdotech.core.presentation.ui.toFormattedMeters
import com.rfdotech.core.presentation.ui.toFormattedPace
import com.rfdotech.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUi(): RunUi {
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - hh:mma")
        .format(dateTimeInLocalTime)

    val distanceKm = DistanceAndSpeedCalculator.getKmFromMeters(distanceMeters)

    return RunUi(
        id = id.orEmpty(),
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKhm.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl
    )
}