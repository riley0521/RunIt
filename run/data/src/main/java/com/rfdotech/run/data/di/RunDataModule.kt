package com.rfdotech.run.data.di

import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.run.data.OfflineFirstRunRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}