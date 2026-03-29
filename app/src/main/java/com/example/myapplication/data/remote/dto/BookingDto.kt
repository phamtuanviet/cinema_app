package com.example.myapplication.data.remote.dto

data class BookingRequest(
    val seatHoldSessionId: String,
    val selectedCombos: Map<String, Int> = emptyMap(),
    val voucherId: String? = null,
    val usedPoints: Int = 0
)

data class BookingResponse(
    val bookingId: String,
    val ticketCode: String,
    val qrCodeUrl: String?,
    val seatAmount: Double,
    val comboAmount: Double,
    val voucherDiscount: Double,
    val pointDiscount: Double,
    val totalAmount: Double,

    val status: String,
    val createdAt: String
)

data class BookingComboRequest(
    val comboId: String,
    val quantity: Int
)

data class BookingDto(
    val id: String,
    val ticketCode : String,
    val qrUrl : String?,
    val status : String,
)