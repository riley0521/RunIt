package com.rfdotech.runtracker

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rfdotech.auth.presentation.intro.IntroScreenRoot
import com.rfdotech.auth.presentation.sign_in.SignInScreenRoot
import com.rfdotech.auth.presentation.sign_up.SignUpScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "auth") {
        authGraph(navController)
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

private fun NavGraphBuilder.runGraph(navController: NavHostController) {
    navigation(
        startDestination = "run_list",
        route = "run"
    ) {
        composable(route = "run_list") {

        }
    }
}