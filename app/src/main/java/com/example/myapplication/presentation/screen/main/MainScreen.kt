package com.example.myapplication.presentation.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.component.BottomBar
import com.example.myapplication.presentation.navigation.graph.cinemaNavGraph
import com.example.myapplication.presentation.navigation.graph.movieNavGraph
import com.example.myapplication.presentation.navigation.graph.profileNavGraph
import com.example.myapplication.presentation.navigation.graph.promotionNavGraph
import com.example.myapplication.presentation.navigation.graph.voucherNavGraph
import com.example.myapplication.presentation.navigation.route.MainRoute

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    val items = listOf(
        MainRoute.MovieGraph,
        MainRoute.CinemaGraph,
        MainRoute.VoucherGraph,
        MainRoute.PromotionGraph,
        MainRoute.ProfileGraph
    )

    Scaffold(

        bottomBar = {
            BottomBar(navController, items)
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