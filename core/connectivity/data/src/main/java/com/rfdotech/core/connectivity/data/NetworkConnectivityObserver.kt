package com.rfdotech.core.connectivity.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.rfdotech.core.connectivity.domain.ConnectivityObserver
import com.rfdotech.core.connectivity.domain.ConnectivityObserver.Status
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkConnectivityObserver(
    private val context: Context
) : ConnectivityObserver {

    private val connectivityManager by lazy {
        context.getSystemService(ConnectivityManager::class.java)
    }

    override fun observe(): Flow<Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(Status.AVAILABLE)
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    trySend(Status.LOSING)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(Status.LOST)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(Status.UNAVAILABLE)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}