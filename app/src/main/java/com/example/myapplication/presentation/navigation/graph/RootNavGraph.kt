package com.example.myapplication.presentation.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myapplication.presentation.app.AppState
import com.example.myapplication.presentation.navigation.route.AuthRoute
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.OnboardingRoute
import com.example.myapplication.presentation.navigation.route.RootRoute
import com.example.myapplication.presentation.screen.splash.SplashScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    appState: AppState
) {

    NavHost(
        navController = navController,
        startDestination = RootRoute.Splash.route
    ) {

        // Splash
        composable(RootRoute.Splash.route) {
            SplashScreen (
                appState,
                onNavigateToOnboarding = {
                    navController.navigate(RootRoute.OnboardingGraph.route) {
                        popUpTo(RootRoute.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(RootRoute.AuthGraph.route) {
                        popUpTo(RootRoute.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(RootRoute.MainGraph.route) {
                        popUpTo(RootRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding
        navigation(
            route = RootRoute.OnboardingGraph.route,
            startDestination = OnboardingRoute.Welcome.route
        ) {
            onboardingNavGraph(navController)
        }

        // Auth
        navigation(
            route = RootRoute.AuthGraph.route,
            startDestination = AuthRoute.Login.route
        ) {
            authNavGraph(navController)
        }

        // Main
        navigation(
            route = RootRoute.MainGraph.route,
            startDestination = MainRoute.BottomGraph.route
        ) {
            mainNavGraph(navController)
        }
    }
}