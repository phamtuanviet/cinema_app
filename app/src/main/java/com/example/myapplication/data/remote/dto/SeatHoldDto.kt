package com.example.myapplication.data.remote.dto

import com.example.myapplication.data.remote.enums.SeatType

data class SeatHoldDto(
    val id: String,
    val seatRow: String,
    val seatNumber: Int,
    val seatType: SeatType,
    val price: Double,
    val expiresAt : String,
    val sessionId : String,
)