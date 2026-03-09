package com.example.myapplication.presentation.navigation.route

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Theaters

sealed class MainRoute(
    val route: String,
    val title: String = "",
    val icon: ImageVector? = null
) {

    // Graph (dùng cho BottomBar)
    object BottomGraph : MainRoute("bottom_graph")

    // Các màn hình chính
    object MovieGraph : MainRoute(
        "movie_graph",
        "Movie",
        Icons.Default.Movie
    )

    object CinemaGraph : MainRoute(
        "cinema_graph",
        "Cinema",
        Icons.Default.Theaters
    )

    object VoucherGraph : MainRoute(
        "voucher_graph",
        "Voucher",
        Icons.Default.ConfirmationNumber
    )

    object PromotionGraph : MainRoute(
        "promotion_graph",
        "Promotion",
        Icons.Default.LocalOffer
    )

    object ProfileGraph : MainRoute(
        "profile_graph",
        "Profile",
        Icons.Default.Person
    )
}