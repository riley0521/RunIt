package com.rfdotech.analytics.data.di

import com.rfdotech.analytics.data.RoomAnalyticsRepository
import com.rfdotech.analytics.domain.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsDataModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
}