package com.example.myapplication.data.remote.dto

data class MovieShowtimeDto(
    val movieId: String,
    val title: String,
    val duration: Int,
    val ageRating: String,
    val posterUrl: String,
    val trailerUrl: String,
    val genres: List<String>,
    val showtimes: List<ShowtimeDto>,
)