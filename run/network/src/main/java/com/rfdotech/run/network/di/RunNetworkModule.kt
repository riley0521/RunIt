package com.rfdotech.run.network.di

import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runNetworkModule = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}