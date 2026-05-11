package com.example.myapplication.data.remote.dto


// Models
data class DashboardStatsDto(
    val totalUsers: Int,
    val totalMovies: Int,
    val totalCinemas: Int,
    val totalShowtimes: Int
)

data class RevenuePointDto(
    val date: String,    // Ví dụ: "10/05", "11/05"
    val amount: Double   // Doanh thu trong ngày
)
