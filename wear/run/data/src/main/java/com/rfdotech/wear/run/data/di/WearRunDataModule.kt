package com.rfdotech.wear.run.data.di

import com.rfdotech.wear.run.data.HealthServicesExerciseTracker
import com.rfdotech.wear.run.domain.ExerciseTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val wearRunDataModule = module {
    singleOf(::HealthServicesExerciseTracker).bind<ExerciseTracker>()
}