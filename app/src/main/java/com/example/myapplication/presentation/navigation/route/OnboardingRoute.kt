package com.example.myapplication.presentation.navigation.route

sealed class OnboardingRoute(val route: String) {
    object Welcome : OnboardingRoute("welcome")
    object Permission : OnboardingRoute("permission")
}