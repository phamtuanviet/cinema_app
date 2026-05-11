package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute

fun NavGraphBuilder.adminVoucherNavGraph(navController: NavHostController) {
    navigation(
        route = AdminRoute.VoucherGraph.route,
        startDestination = AdminRoute.VoucherList.route
    ) {
//        composable(AdminRoute.VoucherList.route) {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Voucher List Screen") }
//        }
    }
}