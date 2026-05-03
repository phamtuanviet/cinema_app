package com.example.myapplication.presentation.navigation.graph

import ProfileMyTicketsScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.ProfileRoute
import com.example.myapplication.presentation.navigation.route.RootRoute
import com.example.myapplication.presentation.screen.profile.account.ProfileAccountScreen
import com.example.myapplication.presentation.screen.profile.change_password.ProfileChangePasswordScreen
import com.example.myapplication.presentation.screen.profile.profile.ProfileScreen
import com.example.myapplication.presentation.screen.profile.settings.ProfileSettingsScreen
import com.example.myapplication.presentation.screen.profile.ticket_detail.ProfileTicketDetailScreen

fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController
) {

    navigation(
        route = MainRoute.ProfileGraph.route,
        startDestination = ProfileRoute.Profile.route
    ) {

        composable(ProfileRoute.Profile.route) {
            ProfileScreen(
                onLogoutSuccess =  {
                    rootNavController.navigate(RootRoute.AuthGraph.route) {
                        popUpTo(0)
                    }
                },
                onNavigateAccount = {
                    navController.navigate(ProfileRoute.Account.route)
                },
                onNavigateBookings = {
                    navController.navigate(ProfileRoute.MyTickets.route)
                },
                onNavigateSettings = {
                    navController.navigate(ProfileRoute.Settings.route)
                }
            )
        }

        composable(ProfileRoute.Settings.route) {
            ProfileSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ProfileRoute.Account.route) {
            ProfileAccountScreen(
                onNavigateToChangePassword = {
                    navController.navigate(ProfileRoute.ChangePassword.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(ProfileRoute.ChangePassword.route) {
            ProfileChangePasswordScreen(
                onSuccess = {
                    navController.navigate(ProfileRoute.Profile.route) {
                        popUpTo(MainRoute.ProfileGraph.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ProfileRoute.MyTickets.route) {
            ProfileMyTicketsScreen(
                onNavigateToDetail = { bookingId ->
                    navController.navigate(ProfileRoute.TicketDetail.route + "/$bookingId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ProfileRoute.TicketDetail.route + "/{bookingId}", // <-- Tên biến ở đây
            arguments = listOf(
                navArgument("bookingId") {
                    type = NavType.StringType
                }
            )
        ) {
            ProfileTicketDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateHome = {
                    navController.popBackStack(MainRoute.MovieGraph.route, inclusive = false)
                }
            )
        }
    }
}