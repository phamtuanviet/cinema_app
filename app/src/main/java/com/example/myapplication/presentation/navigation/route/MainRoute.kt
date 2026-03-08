package com.example.myapplication.presentation.navigation.route

sealed class MainRoute(val route: String) {

    object BottomGraph : MainRoute("bottom_graph")

    object MovieGraph : MainRoute("movie_graph")
    object CinemaGraph : MainRoute("cinema_graph")
    object VoucherGraph : MainRoute("voucher_graph")
    object PromotionGraph : MainRoute("promotion_graph")
    object ProfileGraph : MainRoute("profile_graph")

}