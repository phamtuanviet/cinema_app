package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.presentation.navigation.route.OnboardingRoute
import com.example.myapplication.presentation.navigation.route.RootRoute
import com.example.myapplication.presentation.screen.onboarding.PermissionScreen
import com.example.myapplication.presentation.screen.onboarding.welcome.WelcomeScreen

fun NavGraphBuilder.onboardingNavGraph(
    navController: NavController
) {

    composable(OnboardingRoute.Welcome.route) {
        WelcomeScreen(
            onNavigatePermission = {
                navController.navigate(OnboardingRoute.Permission.route)
            }
        )
    }

    composable(OnboardingRoute.Permission.route) {
        PermissionScreen(
            onAllowPermission = {
                    navController.navigate(RootRoute.AuthGraph.route) {
                        popUpTo(RootRoute.OnboardingGraph.route) { inclusive = true }
                    }
            },
            onSkip = {
                navController.navigate(RootRoute.AuthGraph.route) {
                    popUpTo(RootRoute.OnboardingGraph.route) { inclusive = true }
                }
            }
        )
    }
}