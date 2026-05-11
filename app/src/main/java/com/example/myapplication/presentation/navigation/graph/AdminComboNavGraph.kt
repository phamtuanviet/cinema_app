package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute

fun NavGraphBuilder.adminComboNavGraph(navController: NavHostController) {
    navigation(
        route = AdminRoute.ComboGraph.route,
        startDestination = AdminRoute.ComboList.route
    ) {
//        composable(AdminRoute.ComboList.route) {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Combo List Screen") }
//        }
    }
}