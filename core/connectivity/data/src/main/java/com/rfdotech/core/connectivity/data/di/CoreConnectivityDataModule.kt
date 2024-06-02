package com.rfdotech.core.connectivity.data.di

import com.rfdotech.core.connectivity.data.WearNodeDiscovery
import com.rfdotech.core.connectivity.domain.NodeDiscovery
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreConnectivityDataModule = module {
    singleOf(::WearNodeDiscovery).bind<NodeDiscovery>()
}