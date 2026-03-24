package com.example.myapplication.presentation.navigation.route

sealed class CinemaRoute(val route: String) {

    // danh sách rạp theo thành phố
    object CinemaList : CinemaRoute("cinema_list")

    // chi tiết rạp
    object CinemaDetail : CinemaRoute("cinema_detail/{cinemaId}") {
        fun createRoute(cinemaId: String): String {
            return "cinema_detail/$cinemaId"
        }
    }

    // lịch chiếu tại rạp
    object CinemaShowtime : CinemaRoute("cinema_showtime/{cinemaId}") {
        fun createRoute(cinemaId: String): String {
            return "cinema_showtime/$cinemaId"
        }
    }

    object SeatSelection : CinemaRoute("seat_selection/{showtimeId}") {
        fun createRoute(showtimeId: String): String {
            return "seat_selection/$showtimeId"
        }
    }
    object Checkout : CinemaRoute("checkout/{bookingId}") {
        fun createRoute(bookingId: String): String {
            return "checkout/$bookingId"
        }
    }

}