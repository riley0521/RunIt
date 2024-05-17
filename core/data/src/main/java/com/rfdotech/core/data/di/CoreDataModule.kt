package com.rfdotech.core.data.di

import com.rfdotech.core.data.AndroidDispatcherProvider
import com.rfdotech.core.data.auth.EncryptedSessionStorage
import com.rfdotech.core.data.auth.EncryptedUserStorage
import com.rfdotech.core.data.networking.HttpClientFactory
import com.rfdotech.core.data.run.OfflineFirstRunRepository
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.SessionStorage
import com.rfdotech.core.domain.auth.UserStorage
import com.rfdotech.core.domain.run.RunRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
    singleOf(::EncryptedUserStorage).bind<UserStorage>()
    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
    singleOf(::AndroidDispatcherProvider).bind<DispatcherProvider>()
}