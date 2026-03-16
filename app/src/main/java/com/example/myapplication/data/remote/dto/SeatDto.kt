package com.example.myapplication.data.remote.dto

import com.example.myapplication.data.remote.enums.SeatStatus
import com.example.myapplication.data.remote.enums.SeatType

data class SeatDto(
    val id: String,
    val seatRow: String,
    val seatNumber: Int,
    val seatType: SeatType,
    val price: Double,
    val status: SeatStatus
)