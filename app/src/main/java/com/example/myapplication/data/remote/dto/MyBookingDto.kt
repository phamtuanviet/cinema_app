package com.example.myapplication.data.remote.dto

data class BookingMyBookingDto(
    val id: String,
    val ticketCode: String,
    val qrCodeUrl: String ?,
    val status: String,

    val seatAmount: Double,
    val comboAmount: Double,
    val voucherDiscount: Double,
    val pointDiscount: Double,
    val totalAmount: Double,

    val showtimeStart: String,
    val showtimeEnd: String,

    val movie: MovieMyBookingDto,
    val cinema: CinemaMyBookingDto,
    val room: RoomMyBookingDto,

    val seats: List<SeatMyBookingDto>,
    val combos: List<BookingComboMyBookingDto>,
    val userRating: Int? = null,
    val averageRating: Double? = null,
)

data class MovieMyBookingDto(
    val id: String,
    val title: String,
    val posterUrl: String,
)

data class CinemaMyBookingDto(
    val name: String,
    val address: String,
)

data class RoomMyBookingDto(
    val name: String,
)

data class SeatMyBookingDto(
    val seatRow: String,
    val seatNumber: Int,
)

data class BookingComboMyBookingDto(
    val comboName: String,
    val quantity: Int,
    val price: Double,
)