package com.rfdotech.runtracker

import android.app.Application
import com.rfdotech.auth.data.di.authDataModule
import com.rfdotech.auth.presentation.di.authPresentationModule
import com.rfdotech.core.data.di.coreDataModule
import com.rfdotech.run.presentation.di.runPresentationModule
import com.rfdotech.runtracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RunItApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RunItApp)
            modules(
                authDataModule,
                authPresentationModule,
                coreDataModule,
                runPresentationModule,
                appModule
            )
        }
    }
}