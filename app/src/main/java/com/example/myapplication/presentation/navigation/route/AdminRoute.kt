package com.example.myapplication.presentation.navigation.route

sealed class AdminRoute(val route: String) {
    // ---- TABS CHÍNH ----
    object Dashboard : AdminRoute("admin_dashboard")
    object MoreMenu : AdminRoute("admin_more_menu")

    // ---- CÁC GRAPH CON (MODULES) ----
    object MovieGraph : AdminRoute("admin_movie_graph")
    object CinemaGraph : AdminRoute("admin_cinema_graph")
    object ShowtimeGraph : AdminRoute("admin_showtime_graph")
    object UserGraph : AdminRoute("admin_user_graph")

    object UserList : AdminRoute("admin_user_list")
    object UserDetail : AdminRoute("admin_user_detail/{userId}") {
        fun createRoute(userId: String) = "admin_user_detail/$userId"
    }
    object BookingGraph : AdminRoute("admin_booking_graph")


    object BookingEdit : AdminRoute("admin_booking_edit/{bookingId}") {
        fun createRoute(bookingId: String) = "admin_booking_edit/$bookingId"
    }

    object BookingDetail : AdminRoute("admin_booking_detail/{bookingId}") {
        fun createRoute(bookingId: String) = "admin_booking_detail/$bookingId"
    }

    object ComboGraph : AdminRoute("admin_combo_graph")

    object ComboCreate : AdminRoute("admin_combo_create")

    object ComboEdit : AdminRoute("admin_combo_edit/{comboId}") {
        fun createRoute(comboId: String) = "admin_combo_edit/$comboId"
    }
    object VoucherGraph : AdminRoute("admin_voucher_graph")
    
    object VoucherCreate : AdminRoute("admin_voucher_create")

    object VoucherEdit : AdminRoute("admin_voucher_edit/{voucherId}") {
        fun createRoute(voucherId: String) = "admin_voucher_edit/$voucherId"
    }
    object PaymentGraph : AdminRoute("admin_payment_graph")

    object NewsGraph : AdminRoute("admin_news_graph")

    object NewsList : AdminRoute("admin_news_list")

    object NewsCreate : AdminRoute("admin_news_create")

    object NewsEdit : AdminRoute("admin_news_edit/{newsId}") {
        fun createRoute(newsId: String) = "admin_news_edit/$newsId"
    }

    // ---- MÀN HÌNH CON: MOVIE ----

    object MovieList : AdminRoute("admin_movie_list")
    object MovieCreate : AdminRoute("admin_movie_create")
    object MovieEdit : AdminRoute("admin_movie_edit/{movieId}") {
        fun createRoute(movieId: String) = "admin_movie_edit/$movieId"
    }

    // ---- MÀN HÌNH CON CÁC MODULE KHÁC (Tạm thời chỉ khai báo List) ----
    object CinemaList : AdminRoute("admin_cinema_list")

    object CinemaCreate : AdminRoute("admin_cinema_create")
    object CinemaEdit : AdminRoute("admin_cinema_edit/{cinemaId}") {
        fun createRoute(cinemaId: String) = "admin_cinema_edit/$cinemaId"
    }
    object ShowtimeList : AdminRoute("admin_showtime_list")

    object ShowtimeCreate : AdminRoute("admin_showtime_create")
    object ShowtimeEdit : AdminRoute("admin_showtime_edit/{showtimeId}") {
        fun createRoute(showtimeId: String) = "admin_showtime_edit/$showtimeId"
    }
    object BookingList : AdminRoute("admin_booking_list")
    object ComboList : AdminRoute("admin_combo_list")
    object VoucherList : AdminRoute("admin_voucher_list")
    object PaymentList : AdminRoute("admin_payment_list")

    object BannerGraph : AdminRoute("admin_banner_graph")
    object BannerList : AdminRoute("admin_banner_list")
    object BannerCreate : AdminRoute("admin_banner_create")
    object BannerEdit : AdminRoute("admin_banner_edit/{bannerId}") {
        fun createRoute(bannerId: String) = "admin_banner_edit/$bannerId"
    }
}