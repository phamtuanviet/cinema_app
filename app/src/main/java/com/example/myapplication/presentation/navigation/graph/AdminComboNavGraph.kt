package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.screen.admin.combo.create.AdminComboCreateScreen
import com.example.myapplication.presentation.screen.admin.combo.edit.AdminComboEditScreen
import com.example.myapplication.presentation.screen.admin.combo.list.AdminComboListScreen

fun NavGraphBuilder.adminComboNavGraph(navController: NavHostController) {
    navigation(
        route = AdminRoute.ComboGraph.route,
        startDestination = AdminRoute.ComboList.route
    ) {
        composable(AdminRoute.ComboList.route) {
            AdminComboListScreen(
                onNavigateBack = { navController.popBackStack() }, // Hoặc mở menu Drawer
                onNavigateToCreate = { navController.navigate(AdminRoute.ComboCreate.route) },
                onNavigateToEdit = { comboId ->
                    navController.navigate(AdminRoute.ComboEdit.createRoute(comboId))
                }
            )
        }

        composable(AdminRoute.ComboCreate.route) {
            AdminComboCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() } // Lưu xong quay về list
            )
        }
//
//        // 3. Màn hình Chỉnh sửa Combo
        composable(
            route = AdminRoute.ComboEdit.route,
            arguments = listOf(
                navArgument("comboId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val comboId = backStackEntry.arguments?.getString("comboId") ?: ""
            AdminComboEditScreen(
                comboId = comboId,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )

        }
    }
}