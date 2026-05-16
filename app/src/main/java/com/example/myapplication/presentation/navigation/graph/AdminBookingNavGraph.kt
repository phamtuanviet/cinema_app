package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute
import com.example.myapplication.presentation.screen.admin.booking.detail.AdminBookingDetailScreen
import com.example.myapplication.presentation.screen.admin.booking.list.AdminBookingListScreen

fun NavGraphBuilder.adminBookingNavGraph(navController: NavHostController) {
    navigation(
        route = AdminRoute.BookingGraph.route,
        startDestination = AdminRoute.BookingList.route
    ) {
        composable(AdminRoute.BookingList.route) {
            AdminBookingListScreen(
                onNavigateBack = { navController.popBackStack() },
                // SỬA TÊN HÀM GỌI CHUYỂN TRANG
                onNavigateToDetail = { bookingId ->
                    navController.navigate(AdminRoute.BookingDetail.createRoute(bookingId))
                }
            )
        }

        // MÀN HÌNH CHI TIẾT
        composable(
            route = AdminRoute.BookingDetail.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            AdminBookingDetailScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 2. Màn hình Chi tiết / Chỉnh sửa Đơn đặt vé
//        composable(
//            route = AdminRoute.BookingEdit.route,
//            arguments = listOf(
//                navArgument("bookingId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
//            AdminBookingEditScreen(
//                bookingId = bookingId,
//                onNavigateBack = { navController.popBackStack() },
//                onSaveSuccess = { navController.popBackStack() }
//            )
//        }
    }
}