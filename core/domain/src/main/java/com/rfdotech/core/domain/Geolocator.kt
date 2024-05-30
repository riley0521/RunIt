package com.rfdotech.core.domain

typealias Address = String

interface Geolocator {

    suspend fun getAddressesFromCoordinates(latitude: Double, longitude: Double): List<Address>
}