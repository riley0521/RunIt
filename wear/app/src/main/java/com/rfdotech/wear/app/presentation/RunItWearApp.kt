package com.rfdotech.wear.app.presentation

import android.app.Application
import com.rfdotech.wear.run.presentation.di.wearRunPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RunItWearApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RunItWearApp)
            modules(
                wearRunPresentationModule
            )
        }
    }
}