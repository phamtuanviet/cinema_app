package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute

fun NavGraphBuilder.adminPaymentNavGraph(navController: NavHostController) {
    navigation(
        route = AdminRoute.PaymentGraph.route,
        startDestination = AdminRoute.PaymentList.route
    ) {
//        composable(AdminRoute.PaymentList.route) {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Payment List Screen") }
//        }
    }
}