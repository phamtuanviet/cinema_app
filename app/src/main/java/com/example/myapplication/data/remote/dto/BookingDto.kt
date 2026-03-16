package com.example.myapplication.data.remote.dto

data class CreateBookingRequest(
    val sessionId: String,
    val combos: List<BookingComboRequest>,
    val voucherId: String?,
    val usedPoints: Int
)

data class BookingComboRequest(
    val comboId: String,
    val quantity: Int
)

data class BookingDto(
    val id: String,
    val ticketCode : String,
    val qrUrl : String,
    val status : String,
)