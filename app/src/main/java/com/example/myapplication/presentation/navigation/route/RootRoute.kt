package com.example.myapplication.presentation.navigation.route

sealed class RootRoute(val route: String) {
    object Splash : RootRoute("splash")
    object AuthGraph : RootRoute("auth_graph")
    object MainGraph : RootRoute("main_graph")

    object OnboardingGraph : RootRoute("onboarding_graph")
}