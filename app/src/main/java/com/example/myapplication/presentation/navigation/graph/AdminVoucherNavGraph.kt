package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.screen.admin.voucher.create.AdminVoucherCreateScreen
import com.example.myapplication.presentation.screen.admin.voucher.edit.AdminVoucherEditScreen
import com.example.myapplication.presentation.screen.admin.voucher.list.AdminVoucherListScreen

fun NavGraphBuilder.adminVoucherNavGraph(navController: NavHostController) {
    navigation(
        route = AdminRoute.VoucherGraph.route,
        startDestination = AdminRoute.VoucherList.route
    ) {
        composable(AdminRoute.VoucherList.route) {
            AdminVoucherListScreen(
                onNavigateBack = { navController.popBackStack() }, // Hoặc mở menu Drawer
                onNavigateToCreate = { navController.navigate(AdminRoute.VoucherCreate.route) },
                onNavigateToEdit = { voucherId ->
                    navController.navigate(AdminRoute.VoucherEdit.createRoute(voucherId))
                }
            )
        }

        // 2. Màn hình Thêm Voucher mới
        composable(AdminRoute.VoucherCreate.route) {
            AdminVoucherCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() } // Lưu thành công thì quay về List
            )
        }
//
//        // 3. Màn hình Chỉnh sửa Voucher
        composable(
            route = AdminRoute.VoucherEdit.route,
            arguments = listOf(
                navArgument("voucherId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val voucherId = backStackEntry.arguments?.getString("voucherId") ?: ""
            AdminVoucherEditScreen(
                voucherId = voucherId,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}