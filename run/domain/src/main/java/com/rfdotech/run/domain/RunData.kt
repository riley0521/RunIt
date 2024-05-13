package com.rfdotech.run.domain

import com.rfdotech.core.domain.location.LocationTimestamp
import kotlin.time.Duration

typealias ListOfLocations = List<List<LocationTimestamp>>

data class RunData(
    val distanceMeters: Int = 0,
    val paceInSeconds: Duration = Duration.ZERO,
    val locations: ListOfLocations = emptyList()
)
