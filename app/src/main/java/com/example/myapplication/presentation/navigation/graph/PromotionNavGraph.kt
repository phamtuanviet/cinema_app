package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.PromotionRoute
import com.example.myapplication.presentation.navigation.route.VoucherRoute
import com.example.myapplication.presentation.screen.promotion.promotion_detail.PromotionDetailScreen
import com.example.myapplication.presentation.screen.promotion.promotion_list.PromotionListScreen

fun NavGraphBuilder.promotionNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
) {

    navigation(
        route = MainRoute.PromotionGraph.route,
        startDestination = PromotionRoute.PromotionList.route
    ) {

        composable(PromotionRoute.PromotionList.route) {
            PromotionListScreen(
                onClickDetail = ({ postId ->
                    navController.navigate(PromotionRoute.PromotionDetail.createRoute(postId))
                })
            )
        }

        composable(
            PromotionRoute.PromotionDetail.route,
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.StringType
                }
            )
        ) {
            val postId = it.arguments?.getString("postId").orEmpty()


            PromotionDetailScreen(
                postId = postId,
                onNavigateToVoucher = {
                    navController.navigate(VoucherRoute.VoucherList.route)
                }
            )
        }

    }
}