package com.rfdotech.analytics.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitcompat.SplitCompat
import com.rfdotech.analytics.data.di.analyticsDataModule
import com.rfdotech.analytics.presentation.AnalyticsDashboardScreenRoot
import com.rfdotech.analytics.presentation.di.analyticsPresentationModule
import com.rfdotech.core.presentation.designsystem.RunItTheme
import org.koin.core.context.loadKoinModules

class AnalyticsActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(listOf(analyticsDataModule, analyticsPresentationModule))
        SplitCompat.installActivity(this)

        setContent {
            RunItTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "analytics_dashboard") {
                    composable("analytics_dashboard") {
                        AnalyticsDashboardScreenRoot(onBackClick = { finish() })
                    }
                }
            }
        }
    }
}