package com.example.myapplication.data.remote.dto

data class ShowtimeDetailDto (
    val id : String,
    val cinemaName : String,
    val date : String, // format dd-mm-YYYY
    val time : String, // format HH:mm
    val durationMinutes : Int,
    val room : String,
)