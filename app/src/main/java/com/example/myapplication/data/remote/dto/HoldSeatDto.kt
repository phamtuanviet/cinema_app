package com.example.myapplication.data.remote.dto

import com.example.myapplication.data.remote.enums.SeatType

data class HoldSeatRequest(
    val showtimeId: String,
    val seatId: String,
)

data class HoldSeatResponse(
    val id: String,
    val seatRow: String,
    val seatNumber: Int,
    val seatHoldSessionId :String,
    val price : Double,
    val isSuccess : Boolean,
    val expiresAt : String,
)

data class HoldSeatCoupleRequest(
    val showtimeId: String,
    val firstSeatId: String,
    val secondSeatId : String,
)

data class HoldSeatCoupleResponse(
    val id: String,
    val seatHoldSessionId :String,
    val price : Double,
    val isSuccess : Boolean,
    val expiresAt : String,
)



