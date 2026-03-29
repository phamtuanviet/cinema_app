package com.example.myapplication.presentation.navigation.route

sealed class PromotionRoute(val route: String) {

    // danh sách ưu đãi
    object PromotionList : PromotionRoute("promotion_list")

    // chi tiết ưu đãi
    object PromotionDetail : PromotionRoute("promotion_detail/{postId}") {
        fun createRoute(postId: String): String {
            return "promotion_detail/$postId"
        }
    }

}