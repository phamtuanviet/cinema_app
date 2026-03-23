package com.example.myapplication.data.remote.dto

data class SeatMapDto(
    val showtimeId: String,
    val rows: List<SeatRowDto>,
    val seatHoldSessionId : String ?= null,
    val expiresAt : String ?=  null,
)