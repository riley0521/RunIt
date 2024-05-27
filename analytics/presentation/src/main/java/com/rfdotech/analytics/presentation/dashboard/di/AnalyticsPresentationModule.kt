package com.rfdotech.analytics.presentation.dashboard.di

import com.rfdotech.analytics.presentation.AnalyticsSharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val analyticsPresentationModule = module {
    viewModelOf(::AnalyticsSharedViewModel)
}