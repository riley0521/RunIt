package com.rfdotech.run.location.di

import com.rfdotech.core.domain.Geolocator
import com.rfdotech.run.domain.LocationObserver
import com.rfdotech.run.location.AndroidGeolocator
import com.rfdotech.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runLocationModule = module {
    singleOf(::AndroidLocationObserver).bind<LocationObserver>()
    singleOf(::AndroidGeolocator).bind<Geolocator>()
}