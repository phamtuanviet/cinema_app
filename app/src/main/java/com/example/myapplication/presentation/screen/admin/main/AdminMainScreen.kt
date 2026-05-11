package com.example.myapplication.presentation.screen.admin.main

import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


import androidx.compose.runtime.getValue


import androidx.compose.material.icons.filled.Menu
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.navigation.graph.adminBookingNavGraph
import com.example.myapplication.presentation.navigation.graph.adminCinemaNavGraph
import com.example.myapplication.presentation.navigation.graph.adminComboNavGraph
import com.example.myapplication.presentation.navigation.graph.adminMovieNavGraph
import com.example.myapplication.presentation.navigation.graph.adminPaymentNavGraph
import com.example.myapplication.presentation.navigation.graph.adminShowtimeNavGraph
import com.example.myapplication.presentation.navigation.graph.adminUserNavGraph
import com.example.myapplication.presentation.navigation.graph.adminVoucherNavGraph
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.navigation.route.RootRoute
import com.example.myapplication.presentation.screen.admin.dashboard.AdminDashboardScreen
import com.example.myapplication.presentation.screen.admin.menu.AdminMoreMenuScreen


@Composable
fun AdminMainScreen(
    rootNavController: NavHostController
) {
    val adminNavController = rememberNavController()

    val navBackStackEntry by adminNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomTabs = listOf(
        AdminRoute.Dashboard,
        AdminRoute.MoreMenu
    )


    val showBottomBar = bottomTabs.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                adminNavController.navigate(tab.route) {
                                    popUpTo(adminNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (tab == AdminRoute.Dashboard) Icons.Default.Home else Icons.Default.Menu,
                                    contentDescription = tab.route
                                )
                            },
                            label = {
                                Text(if (tab == AdminRoute.Dashboard) "Dashboard" else "Quản lý")
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = adminNavController,
            startDestination = AdminRoute.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // ---- MÀN HÌNH TAB CHÍNH ----
            composable(AdminRoute.Dashboard.route) {
                AdminDashboardScreen(
                    onNavigateToMore = { adminNavController.navigate(AdminRoute.MoreMenu.route) }
                )
            }



            composable(AdminRoute.MoreMenu.route) {
                AdminMoreMenuScreen(
                    onNavigateToMovies = { adminNavController.navigate(AdminRoute.MovieGraph.route) },
                    onNavigateToCinemas = { adminNavController.navigate(AdminRoute.CinemaGraph.route) },
                    onNavigateToShowtimes = { adminNavController.navigate(AdminRoute.ShowtimeGraph.route) },
                    onNavigateToUsers = { adminNavController.navigate(AdminRoute.UserGraph.route) },
                    onNavigateToBookings = { adminNavController.navigate(AdminRoute.BookingGraph.route) },
                    onNavigateToCombos = { adminNavController.navigate(AdminRoute.ComboGraph.route) },
                    onNavigateToVouchers = { adminNavController.navigate(AdminRoute.VoucherGraph.route) },
                    onNavigateToPayments = { adminNavController.navigate(AdminRoute.PaymentGraph.route) },
                    onLogoutSuccess = {
                        rootNavController.navigate(RootRoute.AuthGraph.route) {
                            popUpTo(0)
                        }
                    }
                )
            }

            // ---- NHÚNG CÁC GRAPH CON (MODULES) ----
            adminMovieNavGraph(adminNavController)
            adminCinemaNavGraph(adminNavController)
            adminShowtimeNavGraph(adminNavController)
            adminUserNavGraph(adminNavController)
            adminBookingNavGraph(adminNavController)
            adminComboNavGraph(adminNavController)
            adminVoucherNavGraph(adminNavController)
            adminPaymentNavGraph(adminNavController)
        }
    }
}