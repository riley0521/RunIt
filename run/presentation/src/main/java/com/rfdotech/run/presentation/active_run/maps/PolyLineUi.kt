package com.rfdotech.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import com.rfdotech.core.domain.location.Location

typealias ListOfPolyLines = List<List<PolyLineUi>>

data class PolyLineUi(
    val location1: Location,
    val location2: Location
)
