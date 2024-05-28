package com.rfdotech.analytics.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.play.core.splitcompat.SplitCompat
import com.rfdotech.analytics.data.di.analyticsDataModule
import com.rfdotech.analytics.domain.AnalyticDetailType
import com.rfdotech.analytics.presentation.dashboard.AnalyticsDashboardScreenRoot
import com.rfdotech.analytics.presentation.AnalyticsSharedViewModel
import com.rfdotech.analytics.presentation.dashboard.di.analyticsPresentationModule
import com.rfdotech.analytics.presentation.detail.AnalyticsDetailScreenRoot
import com.rfdotech.core.presentation.designsystem.RunItTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.loadKoinModules

class AnalyticsActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(listOf(analyticsDataModule, analyticsPresentationModule))
        SplitCompat.installActivity(this)

        setContent {
            RunItTheme {
                val navController = rememberNavController()
                val viewModel: AnalyticsSharedViewModel = koinViewModel()

                NavHost(navController = navController, startDestination = "analytics_dashboard") {
                    composable("analytics_dashboard") {
                        AnalyticsDashboardScreenRoot(
                            onBackClick = { finish() },
                            onNavigateToDetail = { type ->
                                 navController.navigate("analytics_detail/${type.ordinal}")
                            },
                            viewModel = viewModel
                        )
                    }
                    composable(
                        route = "analytics_detail/{type}",
                        arguments = listOf(
                            navArgument("type") {
                                type = NavType.IntType
                                nullable = false
                            }
                        )
                    ) { backStack ->
                        val type = backStack.arguments?.getInt("type") ?: return@composable
                        val analyticDetailType = AnalyticDetailType.entries[type]

                        AnalyticsDetailScreenRoot(
                            analyticDetailType = analyticDetailType,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}