package com.example.myapplication.presentation.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.component.BottomBar
import com.example.myapplication.presentation.navigation.graph.cinemaNavGraph
import com.example.myapplication.presentation.navigation.graph.movieNavGraph
import com.example.myapplication.presentation.navigation.graph.profileNavGraph
import com.example.myapplication.presentation.navigation.graph.promotionNavGraph
import com.example.myapplication.presentation.navigation.graph.voucherNavGraph
import com.example.myapplication.presentation.navigation.route.CinemaRoute
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.MovieRoute
import com.example.myapplication.presentation.navigation.route.ProfileRoute
import com.example.myapplication.presentation.navigation.route.PromotionRoute
import com.example.myapplication.presentation.navigation.route.VoucherRoute

@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val items = listOf(
        MainRoute.MovieGraph,
        MainRoute.CinemaGraph,
        MainRoute.VoucherGraph,
        MainRoute.PromotionGraph,
        MainRoute.ProfileGraph
    )

    val bottomBarRoutes = listOf(
        MovieRoute.MovieList.route,
        CinemaRoute.CinemaList.route,
        VoucherRoute.VoucherList.route,
        PromotionRoute.PromotionList.route,
        ProfileRoute.Profile.route
    )

    Scaffold(
        bottomBar = {

            if (currentRoute in bottomBarRoutes) {
                BottomBar(navController, items)
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = MainRoute.MovieGraph.route,
            modifier = Modifier.padding(padding)
        ) {

            movieNavGraph(navController)
            cinemaNavGraph(navController)
            voucherNavGraph(navController)
            promotionNavGraph(navController)
            profileNavGraph(navController)

        }

    }

}