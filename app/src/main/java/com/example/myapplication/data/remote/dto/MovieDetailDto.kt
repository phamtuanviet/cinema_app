package com.example.myapplication.data.remote.dto


data class MovieDetailDto(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val trailerUrl: String,
    val posterUrl: String,
    val ageRating: String,
    val language: String,
    val genres: List<String>,
    val description: String,
    val releaseDate: String
)