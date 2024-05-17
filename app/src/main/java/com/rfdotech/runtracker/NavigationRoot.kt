package com.rfdotech.runtracker

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.rfdotech.auth.presentation.intro.IntroScreenRoot
import com.rfdotech.auth.presentation.sign_in.SignInScreenRoot
import com.rfdotech.auth.presentation.sign_up.SignUpScreenRoot
import com.rfdotech.run.presentation.active_run.ActiveRunScreenRoot
import com.rfdotech.run.presentation.active_run.service.ActiveRunService
import com.rfdotech.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isSignedIn: Boolean,
    onAnalyticsClick: () -> Unit
) {
    val startDestination = if (isSignedIn) {
        "run"
    } else {
        "auth"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        authGraph(navController)
        runGraph(navController, onAnalyticsClick)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = "intro",
        route = "auth"
    ) {
        composable(route = "intro") {
            IntroScreenRoot(
                onSignUpClick = {
                    navController.navigate("sign_up")
                },
                onSignInClick = {
                    navController.navigate("sign_in")
                },
                onSignInSuccess = {
                    navController.navigate("run") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = "sign_up") {
            SignUpScreenRoot(
                onSignInClick = {
                    navController.navigate("sign_in") {
                        popUpTo("sign_up") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSignUpSuccess = {
                    navController.navigate("sign_in")
                }
            )
        }
        composable(route = "sign_in") {
            SignInScreenRoot(
                onSignUpClick = {
                    navController.navigate("sign_up") {
                        popUpTo("sign_in") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSignInSuccess = {
                    navController.navigate("run") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(
    navController: NavHostController,
    onAnalyticsClick: () -> Unit
) {
    navigation(
        startDestination = "run_overview",
        route = "run"
    ) {
        composable(route = "run_overview") {
            RunOverviewScreenRoot(
                onAnalyticsClick = onAnalyticsClick,
                onStartClick = {
                    navController.navigate("active_run")
                },
                onSignOutClick = {
                    navController.navigate("auth") {
                        popUpTo("run") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = "active_run",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "runit://active_run"
                }
            )
        ) {
            val context = LocalContext.current
            ActiveRunScreenRoot(
                onServiceToggle = { shouldServiceRun ->
                    if (shouldServiceRun) {
                        context.startService(ActiveRunService
                            .createStartIntent(
                                context = context,
                                activityClass = MainActivity::class.java
                            )
                        )
                    } else {
                        context.startService(ActiveRunService.createStopIntent(context))
                    }
                },
                onFinish = {
                    navController.navigateUp()
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}