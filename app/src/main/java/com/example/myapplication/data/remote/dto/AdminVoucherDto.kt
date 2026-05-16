package com.example.myapplication.data.remote.dto

data class AdminVoucherDto(
    val id: String,
    val code: String,
    val discountType: String, // "PERCENT" hoặc "FIXED"
    val discountValue: Double,
    val minOrderValue: Double?,
    val maxDiscount: Double?,
    val expiryDate: String?, // Định dạng: HH:mm - dd/MM/yyyy
    val active: Boolean,
    val usageLimit: Int?,
    val usedCount: Int
)

// 2. Enum định nghĩa 2 Tab Trạng thái
enum class VoucherStatusTab(val title: String, val statusValue: String) {
    VALID("Còn hiệu lực", "VALID"),
    INVALID("Hết hiệu lực", "INVALID")
}

data class AdminVoucherUpdateRequest(
    val code: String,
    val discountType: String,
    val discountValue: Double,
    val minOrderValue: Double?,
    val maxDiscount: Double?,
    val expiryDate: String?, // Gửi định dạng chuẩn ISO: "yyyy-MM-dd'T'HH:mm:ss"
    val active: Boolean,
    val usageLimit: Int?
)

data class AdminVoucherCreateRequest(
    val code: String,
    val discountType: String,
    val discountValue: Double,
    val minOrderValue: Double?,
    val maxDiscount: Double?,
    val expiryDate: String?, // Định dạng ISO: "yyyy-MM-dd'T'HH:mm:ss"
    val active: Boolean,
    val usageLimit: Int?
)