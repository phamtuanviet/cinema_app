package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute

fun NavGraphBuilder.adminBookingNavGraph(navController: NavHostController) {
    navigation(
        route = AdminRoute.BookingGraph.route,
        startDestination = AdminRoute.BookingList.route
    ) {
//        composable(AdminRoute.BookingList.route) {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Booking List Screen") }
//        }
    }
}