package com.example.myapplication.data.remote.dto

data class CinemaShowtimeDto(
    val cinemaId: String,
    val cinemaName: String,
    val distanceKm: Double,
    val showtimes: List<ShowtimeDto>
)