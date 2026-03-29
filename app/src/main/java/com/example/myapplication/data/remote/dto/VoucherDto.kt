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
    val expiryDate: String,
    val isUsable: Boolean
)
data class VoucherPostDto(
    val code: String,
    val discountType: VoucherDiscountType,
    val discountValue: Double,
    val minOrderValue: Double?,
    val maxDiscount: Double?,
    val expiryDate: String?,
    val active: Boolean,
    val usageLimit: Int?,
    val usedCount: Int,
    val remainingUsage: Int?
)

data class UserVoucherResponse(
    val id: String,
    val code: String,

    val discountType: String,
    val discountValue: Double,

    val minOrderValue: Double?,
    val maxDiscount: Double?,

    val expiryDate: String?,
    val isUsed: Boolean,

    // usage info
    val movieTitle: String?,
    val cinemaName: String?,
    val roomName: String?,
    val showtime: String?,

    val discountAmount: Double?,
    val usedAt: String?
)