package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.AdminRoute

fun NavGraphBuilder.adminUserNavGraph(navController: NavHostController) {
    navigation(
        route = AdminRoute.UserGraph.route,
        startDestination = AdminRoute.UserList.route
    ) {
//        composable(AdminRoute.UserList.route) {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("User List Screen") }
//        }
    }
}