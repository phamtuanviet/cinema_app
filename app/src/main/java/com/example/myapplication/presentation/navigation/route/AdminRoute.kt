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
    object BookingGraph : AdminRoute("admin_booking_graph")
    object ComboGraph : AdminRoute("admin_combo_graph")
    object VoucherGraph : AdminRoute("admin_voucher_graph")
    object PaymentGraph : AdminRoute("admin_payment_graph")

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
    object UserList : AdminRoute("admin_user_list")
    object BookingList : AdminRoute("admin_booking_list")
    object ComboList : AdminRoute("admin_combo_list")
    object VoucherList : AdminRoute("admin_voucher_list")
    object PaymentList : AdminRoute("admin_payment_list")
}