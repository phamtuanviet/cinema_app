package com.example.myapplication.data.remote.dto

data class RatingRequest(
    val movieId: String,
    val score: Int
)

data class RatingResponse(
    val movieId: String,
    val userScore: Int,
    val averageScore: Double
)