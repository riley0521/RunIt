package com.rfdotech.wear.app.presentation

import android.app.Application
import com.rfdotech.core.data.di.coreDataModule
import com.rfdotech.wear.app.BuildConfig
import com.rfdotech.wear.run.data.di.wearRunDataModule
import com.rfdotech.wear.run.presentation.di.wearRunPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RunItWearApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RunItWearApp)
            modules(
                wearRunDataModule,
                wearRunPresentationModule,
                coreDataModule
            )
        }
    }
}