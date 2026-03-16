package com.example.myapplication.data.remote.dto

data class SeatMapDto(
    val showtimeId: String,
    val rows: List<SeatRowDto>,
    val expiresAt: String?,
    val sessionId : String?
)