package com.rfdotech.run.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.rfdotech.core.domain.Address
import com.rfdotech.core.domain.Geolocator
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidGeolocator(
    private val context: Context
): Geolocator {

    companion object {
        private const val MAX_ITEMS = 3
    }

    override suspend fun getAddressesFromCoordinates(
        latitude: Double,
        longitude: Double
    ): List<Address> = suspendCancellableCoroutine { continuation ->
        val geocoder = Geocoder(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val listener = object: Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<android.location.Address>) {
                    continuation.resume(addresses.map { it.toAddressLine1() })
                }

                override fun onError(errorMessage: String?) {
                    super.onError(errorMessage)
                    Log.d("AndroidGeolocator", errorMessage ?: "Unknown error.")
                    continuation.resume(emptyList())
                }
            }
            geocoder.getFromLocation(latitude, longitude, MAX_ITEMS, listener)
        } else {
            val addresses = geocoder.getFromLocation(latitude, longitude, MAX_ITEMS)

            continuation.resume(addresses?.map { it.toAddressLine1() }.orEmpty())
        }
    }
}