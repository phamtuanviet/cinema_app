package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.screen.admin.cinema.create.AdminCinemaCreateScreen
import com.example.myapplication.presentation.screen.admin.cinema.edit.AdminCinemaEditScreen
import com.example.myapplication.presentation.screen.admin.cinema.list.AdminCinemaListScreen

fun NavGraphBuilder.adminCinemaNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AdminRoute.CinemaGraph.route,
        startDestination = AdminRoute.CinemaList.route
    ) {

        // 1. Danh sách Rạp (Phân trang + Tìm kiếm)
        composable(AdminRoute.CinemaList.route) {
            AdminCinemaListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = { navController.navigate(AdminRoute.CinemaCreate.route) },
                onNavigateToEdit = { cinemaId ->
                    navController.navigate(AdminRoute.CinemaEdit.createRoute(cinemaId))
                }
            )
        }

//        // 2. Thêm Rạp mới
        composable(AdminRoute.CinemaCreate.route) {
            AdminCinemaCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // 3. Chỉnh sửa Rạp
        composable(
            route = AdminRoute.CinemaEdit.route,
            arguments = listOf(
                navArgument("cinemaId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cinemaId = backStackEntry.arguments?.getString("cinemaId") ?: ""
            AdminCinemaEditScreen(
                cinemaId = cinemaId,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}