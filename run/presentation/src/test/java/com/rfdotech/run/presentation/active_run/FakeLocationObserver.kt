package com.rfdotech.run.presentation.active_run

import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.location.LocationWithAltitude
import com.rfdotech.run.domain.LocationObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

val FAKE_LOCATION = LocationWithAltitude(
    location = Location(
        latitude = 14.624703,
        longitude = 121.120091
    ), altitude = 1.0
)

val FAKE_LOCATION_2 = LocationWithAltitude(
    location = Location(
        latitude = 14.624503,
        longitude = 121.119298
    ), altitude = 1.0
)

class FakeLocationObserver : LocationObserver {

    private val myFlow = MutableStateFlow(FAKE_LOCATION)

    override fun observeLocation(interval: Long): Flow<LocationWithAltitude> {
        return myFlow
    }

    fun emitAnotherLocation() {
        myFlow.update { FAKE_LOCATION_2 }
    }
}