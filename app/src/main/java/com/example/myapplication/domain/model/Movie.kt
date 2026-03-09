package com.example.myapplication.domain.model

data class Movie(
    val id: String,
    val title: String,
    val ageRating: String,
    val posterUrl: String,
    val genres: List<String>,
    val duration: String,
    val language: String,
)