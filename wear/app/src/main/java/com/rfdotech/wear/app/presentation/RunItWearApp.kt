package com.rfdotech.wear.app.presentation

import android.app.Application
import com.rfdotech.core.connectivity.data.di.coreConnectivityDataModule
import com.rfdotech.core.data.di.coreDataModule
import com.rfdotech.wear.app.BuildConfig
import com.rfdotech.wear.app.di.appModule
import com.rfdotech.wear.run.data.di.wearRunDataModule
import com.rfdotech.wear.run.presentation.di.wearRunPresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RunItWearApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RunItWearApp)
            modules(
                appModule,
                wearRunDataModule,
                wearRunPresentationModule,
                coreConnectivityDataModule,
                coreDataModule
            )
        }
    }
}