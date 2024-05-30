package com.rfdotech.run.presentation.run_overview

import com.rfdotech.core.domain.Address
import com.rfdotech.core.domain.Geolocator

class FakeGeolocator : Geolocator {

    var isError = false

    override suspend fun getAddressesFromCoordinates(
        latitude: Double,
        longitude: Double
    ): List<Address> {
        if (isError) {
            throw Exception()
        } else {
            return listOf("Address1")
        }
    }
}