package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.screen.admin.user.detail.AdminUserDetailScreen
import com.example.myapplication.presentation.screen.admin.user.list.AdminUserListScreen

fun NavGraphBuilder.adminUserNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AdminRoute.UserGraph.route,
        startDestination = AdminRoute.UserList.route
    ) {

        // 1. Màn hình Danh sách Người dùng (Có tìm kiếm)
        composable(AdminRoute.UserList.route) {
            AdminUserListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { userId -> navController.navigate(AdminRoute.UserDetail.createRoute(userId)) }
            )
        }

        // 2. Màn hình Chỉnh sửa Người dùng
        composable(
            route = AdminRoute.UserDetail.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            AdminUserDetailScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}