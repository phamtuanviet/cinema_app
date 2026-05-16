package com.example.myapplication.data.remote.dto

data class BannerDto(
    val id: String,
    val imageUrl: String,
    val actionType: String, // Trả về "URL" hoặc "MOVIE"
    val targetUrl: String?,
    val movieId: String?
)