package com.example.myapplication.data.remote.dto

data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val number: Int
)