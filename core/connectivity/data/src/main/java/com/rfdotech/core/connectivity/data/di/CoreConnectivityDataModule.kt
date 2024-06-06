package com.rfdotech.core.connectivity.data.di

import com.rfdotech.core.connectivity.data.NetworkConnectivityObserver
import com.rfdotech.core.connectivity.data.WearNodeDiscovery
import com.rfdotech.core.connectivity.data.messaging.WearMessagingClient
import com.rfdotech.core.connectivity.domain.ConnectivityObserver
import com.rfdotech.core.connectivity.domain.NodeDiscovery
import com.rfdotech.core.connectivity.domain.messaging.MessagingClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreConnectivityDataModule = module {
    singleOf(::WearNodeDiscovery).bind<NodeDiscovery>()
    singleOf(::WearMessagingClient).bind<MessagingClient>()
    singleOf(::NetworkConnectivityObserver).bind<ConnectivityObserver>()
}