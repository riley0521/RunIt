package com.rfdotech.wear.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rfdotech.core.notification.ActiveRunService
import com.rfdotech.core.presentation.designsystem_wear.RunItWearTheme
import com.rfdotech.wear.run.presentation.TrackerScreenRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            RunItWearTheme {
                TrackerScreenRoot(
                    onServiceToggle = { isServiceRunning ->
                        if (isServiceRunning) {
                            startService(
                                ActiveRunService.createStartIntent(
                                    context = applicationContext,
                                    activityClass = MainActivity::class.java
                                )
                            )
                        } else {
                            startService(
                                ActiveRunService.createStopIntent(applicationContext)
                            )
                        }
                    }
                )
            }
        }
    }
}