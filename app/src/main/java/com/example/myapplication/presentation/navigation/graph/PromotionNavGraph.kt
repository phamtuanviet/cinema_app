package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.PromotionRoute
import com.example.myapplication.presentation.screen.promotion.promotion_detail.PromotionDetailScreen
import com.example.myapplication.presentation.screen.promotion.promotion_list.PromotionListScreen

fun NavGraphBuilder.promotionNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController
) {

    navigation(
        route = MainRoute.PromotionGraph.route,
        startDestination = PromotionRoute.PromotionList.route
    ) {

        composable(PromotionRoute.PromotionList.route) {
            PromotionListScreen()
        }

        composable(PromotionRoute.PromotionDetail.route) {
            PromotionDetailScreen()
        }

    }
}