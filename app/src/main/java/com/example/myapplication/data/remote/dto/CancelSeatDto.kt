package com.example.myapplication.data.remote.dto

data class CancelSeatRequest(
    val showtimeId: String,
    val seatId: String,
)

data class CancelSeatResponse(
    val id: String,
    val seatRow: String,
    val seatNumber: Int,
    val seatHoldSessionId :String,
    val isSuccess : Boolean,
    val expiresAt : String,
    val price : Double,
)

data class CancelSeatCoupleRequest(
    val showtimeId: String,
    val firstSeatId: String,
    val secondSeatId : String,
)

data class CancelSeatCoupleResponse(
    val id: String,
    val seatHoldSessionId :String,
    val price : Double,
    val isSuccess : Boolean,
    val expiresAt : String,
)
