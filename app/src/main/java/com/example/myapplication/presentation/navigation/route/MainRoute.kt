

package com.example.myapplication.presentation.navigation.route

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainRoute(
    val route: String,
    val title: String = "",
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    // Graph (dùng cho BottomBar)
    object BottomGraph : MainRoute("bottom_graph")

    // Các màn hình chính
    object MovieGraph : MainRoute(
        route = "movie_graph",
        title = "Phim",
        selectedIcon = Icons.Filled.Movie,
        unselectedIcon = Icons.Outlined.Movie
    )

    object CinemaGraph : MainRoute(
        route = "cinema_graph",
        title = "Rạp",
        selectedIcon = Icons.Filled.Theaters,
        unselectedIcon = Icons.Outlined.Theaters
    )

    object VoucherGraph : MainRoute(
        route = "voucher_graph",
        title = "Voucher",
        selectedIcon = Icons.Filled.ConfirmationNumber,
        unselectedIcon = Icons.Outlined.ConfirmationNumber
    )

    object PromotionGraph : MainRoute(
        route = "promotion_graph",
        title = "Ưu đãi", // Rút gọn chữ để UI thoáng hơn
        selectedIcon = Icons.Filled.LocalOffer, // Hoặc CardGiftcard
        unselectedIcon = Icons.Outlined.LocalOffer
    )

    object ProfileGraph : MainRoute(
        route = "profile_graph",
        title = "Cá nhân",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}