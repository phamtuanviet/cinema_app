package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.VoucherRoute
import com.example.myapplication.presentation.screen.voucher.add_voucher.AddVoucherScreen
import com.example.myapplication.presentation.screen.voucher.voucher_history.VoucherHistoryScreen
import com.example.myapplication.presentation.screen.voucher.voucher_list.VoucherListScreen

fun NavGraphBuilder.voucherNavGraph(
    navController: NavHostController
) {

    navigation(
        route = MainRoute.VoucherGraph.route,
        startDestination = VoucherRoute.VoucherList.route
    ) {

        composable(VoucherRoute.VoucherList.route) {
            VoucherListScreen()
        }

        composable(VoucherRoute.AddVoucher.route) {
            AddVoucherScreen()
        }

        composable(VoucherRoute.VoucherHistory.route) {
            VoucherHistoryScreen()
        }
    }
}