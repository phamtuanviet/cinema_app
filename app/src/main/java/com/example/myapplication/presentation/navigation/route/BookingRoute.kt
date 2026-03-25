package com.example.myapplication.presentation.navigation.route

sealed class BookingRoute(val route: String) {

    object SeatSelection : BookingRoute(
        "booking_seat_selection/{showtimeId}/{movieId}"
    ) {
        fun createRoute(showtimeId: String, movieId: String): String {
            return "booking_seat_selection/$showtimeId/$movieId"
        }
    }

    object OtherOptions : BookingRoute(
        "booking_other_options/{seatHoldSessionId}"
    ) {
        fun createRoute(seatHoldSessionId: String): String {
            return "booking_other_options/$seatHoldSessionId"
        }
    }

    object Checkout : BookingRoute(
        "booking_checkout/{bookingId}"
    ) {
        fun createRoute(bookingId: String): String {
            return "booking_checkout/$bookingId"
        }
    }

    object PaymentSuccess : BookingRoute(
        "booking_payment_success"
    )

    object PaymentFailed : BookingRoute(
        "booking_payment_failed"
    )
}