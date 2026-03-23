package com.example.myapplication.data.remote.dto

import java.math.BigDecimal

data class ComboDto(
    val id: String,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val imageUrl: String?,
    val isAvailable: Boolean
)