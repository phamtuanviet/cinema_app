package com.example.myapplication.data.remote.dto


data class MovieDto(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val posterUrl: String,
    val ageRating: String,
    val language: String,
    val trailerUrl : String,
    val releaseDate: String,
    val description: String,
    val genres: List<String>
)