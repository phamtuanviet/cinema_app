package com.example.myapplication.data.remote.dto

data class CinemaResponse(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double,
    val logoUrl : String
)