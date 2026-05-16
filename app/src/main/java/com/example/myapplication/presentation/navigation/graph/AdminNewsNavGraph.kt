package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.screen.admin.news.edit.AdminNewsEditScreen
import com.example.myapplication.presentation.screen.admin.news.list.AdminNewsListScreen

fun NavGraphBuilder.adminNewsNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AdminRoute.NewsGraph.route,
        startDestination = AdminRoute.NewsList.route
    ) {

        // 1. Màn hình Danh sách Tin tức
        composable(AdminRoute.NewsList.route) {
            AdminNewsListScreen(
                onNavigateBack = { navController.popBackStack() }, // Hoặc bật Drawer
                onNavigateToCreate = { navController.navigate(AdminRoute.NewsCreate.route) },
                onNavigateToEdit = { newsId ->
                    navController.navigate(AdminRoute.NewsEdit.createRoute(newsId))
                }
            )
        }

        // 2. Màn hình Thêm Tin tức mới
//        composable(AdminRoute.NewsCreate.route) {
//            AdminNewsCreateScreen(
//                onNavigateBack = { navController.popBackStack() },
//                onSaveSuccess = { navController.popBackStack() } // Lưu xong quay về list
//            )
//        }
//
//        // 3. Màn hình Chỉnh sửa Tin tức
        composable(
            route = AdminRoute.NewsEdit.route,
            arguments = listOf(
                navArgument("newsId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
            AdminNewsEditScreen(
                newsId = newsId,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}