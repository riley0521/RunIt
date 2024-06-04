package com.rfdotech.wear.run.presentation.di

import com.rfdotech.wear.run.domain.RunningTracker
import com.rfdotech.wear.run.presentation.TrackerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val wearRunPresentationModule = module {
    singleOf(::RunningTracker)
    single {
        get<RunningTracker>().elapsedTime
    }
    viewModelOf(::TrackerViewModel)
}