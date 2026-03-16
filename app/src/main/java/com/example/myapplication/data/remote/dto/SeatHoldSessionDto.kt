package com.example.myapplication.data.remote.dto

data class SeatHoldSessionDto(
    val sessionId: String,
    val showtimeId: String,
    val expiresAt: String,
    val seats: List<SeatHoldDto>,
    val totalPrice: Int
)