package com.rfdotech.runtracker

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rfdotech.auth.presentation.intro.IntroScreenRoot
import com.rfdotech.auth.presentation.registration.RegistrationScreenRoot

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
            RegistrationScreenRoot(
                onSignInClick = {
                    navController.navigate("sign_in") {
                        popUpTo("sign_up") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate("sign_in")
                }
            )
        }
        composable(route = "sign_in") {

        }
    }
}