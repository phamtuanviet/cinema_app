package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.screen.admin.movie.create.AdminMovieCreateScreen
import com.example.myapplication.presentation.screen.admin.movie.edit.AdminMovieEditScreen
import com.example.myapplication.presentation.screen.admin.movie.list.AdminMovieListScreen

fun NavGraphBuilder.adminMovieNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AdminRoute.MovieGraph.route,
        startDestination = AdminRoute.MovieList.route
    ) {

        composable(AdminRoute.MovieList.route) {
            AdminMovieListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCreate = {
                    navController.navigate(AdminRoute.MovieCreate.route)
                },
                onNavigateToEdit = { movieId ->
                    navController.navigate(AdminRoute.MovieEdit.createRoute(movieId))
                }
            )
        }

        // 2. MÀN HÌNH THÊM PHIM MỚI
        composable(AdminRoute.MovieCreate.route) {
            AdminMovieCreateScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    // Khi lưu thành công, quay về màn danh sách và load lại
                    navController.popBackStack()
                }
            )
        }
//
//        // 3. MÀN HÌNH CHỈNH SỬA PHIM
        composable(
            route = AdminRoute.MovieEdit.route,
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""

            AdminMovieEditScreen(
                movieId = movieId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}