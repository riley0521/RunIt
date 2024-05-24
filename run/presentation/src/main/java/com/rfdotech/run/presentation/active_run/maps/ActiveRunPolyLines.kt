package com.rfdotech.run.presentation.active_run.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline
import com.rfdotech.core.presentation.designsystem.colorPrimary
import com.rfdotech.run.domain.ListOfLocations

@Composable
fun ActiveRunPolyLines(
    locations: ListOfLocations
) {
    val polyLines = remember(locations) {
        locations.map { locations ->
            locations.zipWithNext { timestamp1, timestamp2 ->
                PolyLineUi(
                    location1 = timestamp1.location.location,
                    location2 = timestamp2.location.location
                )
            }
        }
    }

    polyLines.forEach { polyLine ->
        polyLine.forEach { data ->
            Polyline(
                points = listOf(
                    LatLng(data.location1.latitude, data.location1.longitude),
                    LatLng(data.location2.latitude, data.location2.longitude)
                ),
                color = colorPrimary,
                jointType = JointType.BEVEL
            )
        }
    }
}