package com.example.myapplication.data.remote.dto

import com.example.myapplication.data.remote.enums.VoucherDiscountType
import java.math.BigDecimal
import java.time.LocalDateTime


data class VoucherDto(

    val id: String,
    val code: String,
    val title: String,
    val description: String?,
    val discountType: VoucherDiscountType,
    val discountValue: BigDecimal,
    val maxDiscount: BigDecimal?,
    val minOrderAmount: BigDecimal?,
    val expiryDate: LocalDateTime,
    val isUsable: Boolean
)