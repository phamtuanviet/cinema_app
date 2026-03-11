package com.example.myapplication.presentation.navigation.route


sealed class MovieRoute(val route: String) {
    object MovieList : MovieRoute("movie_list")

    object MovieBooking : MovieRoute("movie_booking/{movieId}") {
        fun createRoute(movieId: String): String {
            return "movie_booking/$movieId"
        }
    }
    object MovieDetail : MovieRoute("movie_detail/{movieId}") {
        fun createRoute(movieId: String): String {
            return "movie_detail/$movieId"
        }
    }
    object Showtime : MovieRoute("movie_showtime/{movieId}") {
        fun createRoute(movieId: String): String {
            return "movie_showtime/$movieId"
        }
    }
    object SeatSelection : MovieRoute("seat_selection/{showtimeId}") {
        fun createRoute(showtimeId: String): String {
            return "seat_selection/$showtimeId"
        }
    }
    object Checkout : MovieRoute("checkout/{bookingId}") {
        fun createRoute(bookingId: String): String {
            return "checkout/$bookingId"
        }
    }

}