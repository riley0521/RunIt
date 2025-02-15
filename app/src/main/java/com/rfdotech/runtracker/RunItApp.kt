package com.rfdotech.runtracker

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.rfdotech.auth.data.di.authDataModule
import com.rfdotech.auth.presentation.di.authPresentationModule
import com.rfdotech.core.connectivity.data.di.coreConnectivityDataModule
import com.rfdotech.core.data.di.coreDataModule
import com.rfdotech.core.database.di.coreDatabaseModule
import com.rfdotech.core.presentation.ui.di.corePresentationUiModule
import com.rfdotech.run.data.di.runDataModule
import com.rfdotech.run.location.di.runLocationModule
import com.rfdotech.run.network.di.runNetworkModule
import com.rfdotech.run.presentation.di.runPresentationModule
import com.rfdotech.runtracker.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class RunItApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RunItApp)
            workManagerFactory()
            modules(
                authDataModule,
                authPresentationModule,
                coreConnectivityDataModule,
                coreDataModule,
                coreDatabaseModule,
                corePresentationUiModule,
                runDataModule,
                runLocationModule,
                runNetworkModule,
                runPresentationModule,
                appModule
            )
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}