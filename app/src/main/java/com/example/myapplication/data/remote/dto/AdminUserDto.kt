package com.example.myapplication.data.remote.dto

import javax.inject.Inject
import retrofit2.http.GET
import retrofit2.http.Query

// 1. DTO hiển thị danh sách người dùng


// 2. Enum cho 2 Tab
enum class UserRoleTab(val title: String, val roleFilter: String) {
    ADMIN("Quản trị viên", "ADMIN"),
    USER("Khách hàng", "USER")
}


data class AdminUserDto(
    val id: String,
    val email: String,
    val fullName: String?,
    val phone: String?,
    val isVerified: Boolean,
    val role: String,
    val avatarUrl: String?,
    val isBanned: Boolean // 🔥 Thêm trường mới
)

data class AdminUserUpdateRequest(
    val role: String,
    val isBanned: Boolean,
    val isVerified: Boolean
)

data class AdminUserVoucherDto(
    val id: String,
    val voucherCode: String,
    val discountType: String,
    val discountValue: Double,
    val isUsed: Boolean,
    val usedAt: String?
)

data class AdminLoyaltyTransactionDto(
    val id: String,
    val points: Int, // Số âm là trừ điểm (sử dụng), số dương là cộng điểm
    val type: String, // EARN, SPEND, REFUND...
    val description: String,
    val createdAt: String
)

data class AdminUserDetailDto(
    val id: String,
    val fullName: String?,
    val email: String,
    val avatarUrl: String?,
    val availablePoints: Int,
    val userVouchers: List<AdminUserVoucherDto>,
    val loyaltyTransactions: List<AdminLoyaltyTransactionDto>
)

enum class UserDetailTab(val title: String) {
    VOUCHER("Túi Voucher"),
    LOYALTY("Điểm Thưởng")
}

