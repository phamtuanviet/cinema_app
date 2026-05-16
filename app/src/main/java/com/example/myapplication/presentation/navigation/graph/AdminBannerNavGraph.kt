package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.screen.admin.banner.create.AdminBannerCreateScreen
import com.example.myapplication.presentation.screen.admin.banner.edit.AdminBannerEditScreen
import com.example.myapplication.presentation.screen.admin.banner.list.AdminBannerListScreen

fun NavGraphBuilder.adminBannerNavGraph(navController: NavHostController) {
    navigation(
        route = AdminRoute.BannerGraph.route,
        startDestination = AdminRoute.BannerList.route
    ) {
        // 1. Màn hình danh sách Banner
        composable(AdminRoute.BannerList.route) {
            AdminBannerListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = { navController.navigate(AdminRoute.BannerCreate.route) },
                onNavigateToEdit = { bannerId ->
                    navController.navigate(AdminRoute.BannerEdit.createRoute(bannerId))
                }
            )
        }

        // 2. Màn hình tạo mới Banner
        composable(AdminRoute.BannerCreate.route) {
            AdminBannerCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
//
//        // 3. Màn hình chỉnh sửa Banner
        composable(
            route = AdminRoute.BannerEdit.route,
            arguments = listOf(navArgument("bannerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bannerId = backStackEntry.arguments?.getString("bannerId") ?: ""
            AdminBannerEditScreen(
                bannerId = bannerId,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}