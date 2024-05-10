package com.rfdotech.run.location

import com.rfdotech.core.domain.location.Location
import android.location.Location as AndroidLocation
import com.rfdotech.core.domain.location.LocationWithAltitude

fun AndroidLocation.toLocationWithAltitude(): LocationWithAltitude {
    return LocationWithAltitude(
        location = Location(
            latitude = latitude,
            longitude = longitude
        ),
        altitude = altitude
    )
}