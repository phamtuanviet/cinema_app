package com.example.myapplication.data.remote.dto

data class ReleaseSeatRequestDto(

    val showtimeId: String,

    val seatIds: List<String>

)