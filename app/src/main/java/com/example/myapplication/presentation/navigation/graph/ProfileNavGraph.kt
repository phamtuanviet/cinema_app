package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.ProfileRoute
import com.example.myapplication.presentation.screen.profile.my_tickets.ProfileMyTicketsScreen
import com.example.myapplication.presentation.screen.profile.notification.ProfileNotificationScreen
import com.example.myapplication.presentation.screen.profile.profile.ProfileScreen
import com.example.myapplication.presentation.screen.profile.settings.ProfileSettingsScreen

fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController
) {

    navigation(
        route = MainRoute.ProfileGraph.route,
        startDestination = ProfileRoute.Profile.route
    ) {

        composable(ProfileRoute.Profile.route) {
            ProfileScreen()
        }

        composable(ProfileRoute.Settings.route) {
            ProfileSettingsScreen()
        }

        composable(ProfileRoute.Notification.route) {
            ProfileNotificationScreen()
        }

        composable(ProfileRoute.MyTickets.route) {
            ProfileMyTicketsScreen()
        }
    }
}