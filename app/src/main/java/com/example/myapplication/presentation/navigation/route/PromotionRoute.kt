package com.example.myapplication.presentation.navigation.route

sealed class PromotionRoute(val route: String) {

    // danh sách ưu đãi
    object PromotionList : PromotionRoute("promotion_list")

    // chi tiết ưu đãi
    object PromotionDetail : PromotionRoute("promotion_detail/{promotionId}") {
        fun createRoute(promotionId: Int): String {
            return "promotion_detail/$promotionId"
        }
    }

}