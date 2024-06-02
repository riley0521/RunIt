package com.rfdotech.wear.app.di

import com.rfdotech.wear.app.presentation.RunItWearApp
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single {
        (androidApplication() as RunItWearApp).applicationScope
    }
}