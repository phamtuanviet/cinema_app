package com.example.myapplication.data.remote.dto

import com.example.myapplication.data.remote.enums.SeatType

data class HoldSeatRequest(
    val showtimeId: String,
    val seatId: String,
    val sessionId: String?
)

data class HoldSeatResponse(
    val id: String,
    val seatRow: String,
    val seatNumber: Int,
    val seatType: SeatType,
    val price: Double,
    val expiresAt : String,
    val sessionId : String,
)