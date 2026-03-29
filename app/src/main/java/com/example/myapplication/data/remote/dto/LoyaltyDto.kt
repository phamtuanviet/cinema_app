package com.example.myapplication.data.remote.dto

data class LoyaltyAccountResponse(
    val availablePoints: Int
)

data class LoyaltyTransactionResponse(
    val points: Int,
    val type: String,
    val description: String,

    val movieTitle: String?,
    val cinemaName: String?,
    val showtime: String?,

    val createdAt: String
)