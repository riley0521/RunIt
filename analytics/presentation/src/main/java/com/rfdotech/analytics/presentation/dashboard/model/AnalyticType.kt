package com.rfdotech.analytics.presentation.dashboard.model

import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import com.rfdotech.core.domain.run.Run

typealias RunAndValueMap = Map<Run, Number>

sealed interface AnalyticType {
    data class Distance(val data: List<Run>): AnalyticType
    data class Pace(val data: List<Run>): AnalyticType

    fun getData(): RunAndValueMap {
        return when (this) {
            is Distance -> {
                data.associateWith { run ->
                    val distanceKm = DistanceAndSpeedCalculator.getKmFromMeters(run.distanceMeters).toFloat()
                    distanceKm
                }
            }
            is Pace -> {
                data.associateWith { run ->
                    run.duration.inWholeSeconds
                }
            }
        }
    }
}