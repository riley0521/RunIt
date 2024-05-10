package com.rfdotech.run.location.di

import com.rfdotech.run.domain.LocationObserver
import com.rfdotech.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runLocationModule = module {
    singleOf(::AndroidLocationObserver).bind<LocationObserver>()
}