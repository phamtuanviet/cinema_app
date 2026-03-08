package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigation
import com.example.myapplication.presentation.navigation.route.MainRoute

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {

    navigation(
        route = MainRoute.BottomGraph.route,
        startDestination = MainRoute.MovieGraph.route
    ) {
        movieNavGraph(navController)
        cinemaNavGraph(navController)
        voucherNavGraph(navController)
        promotionNavGraph(navController)
        profileNavGraph(navController)
    }
}