package com.example.myapplication.data.remote.dto

data class SeatHoldSessionInfoDto(
    val movie : MovieDto,
    val showtime : ShowtimeDetailDto,
    val combos : List<ComboDto>,
    val vouchers : List<VoucherDto>,
    val availablePoints : Int,
    val seatAmount :  Double,
)