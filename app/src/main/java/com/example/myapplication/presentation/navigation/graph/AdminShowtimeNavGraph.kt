package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.screen.admin.showtime.create.AdminShowtimeCreateScreen
import com.example.myapplication.presentation.screen.admin.showtime.edit.AdminShowtimeEditScreen

import com.example.myapplication.presentation.screen.admin.showtime.list.AdminShowtimeListScreen

fun NavGraphBuilder.adminShowtimeNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AdminRoute.ShowtimeGraph.route,
        startDestination = AdminRoute.ShowtimeList.route
    ) {

        // 1. Màn hình Danh sách Lịch chiếu
        composable(AdminRoute.ShowtimeList.route) {
            AdminShowtimeListScreen(
                onNavigateBack = { navController.popBackStack() }, // Hoặc bật Drawer tùy thiết kế của bạn
                onNavigateToCreate = { navController.navigate(AdminRoute.ShowtimeCreate.route) },
                onNavigateToEdit = { showtimeId ->
                    navController.navigate(AdminRoute.ShowtimeEdit.createRoute(showtimeId))
                }
            )
        }

        composable(AdminRoute.ShowtimeCreate.route) {
            AdminShowtimeCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() } // Lưu xong thì quay về list
            )
        }
//
        composable(
            route = AdminRoute.ShowtimeEdit.route,
            arguments = listOf(
                navArgument("showtimeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val showtimeId = backStackEntry.arguments?.getString("showtimeId") ?: ""
            AdminShowtimeEditScreen(
                showtimeId = showtimeId,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}