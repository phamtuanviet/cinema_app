package com.example.myapplication.data.remote.dto

import java.math.BigDecimal

data class BookingPreviewDto(

    val seatAmount: BigDecimal,

    val comboAmount: BigDecimal,

    val voucherDiscount: BigDecimal,

    val pointDiscount: BigDecimal,

    val totalAmount: BigDecimal
)