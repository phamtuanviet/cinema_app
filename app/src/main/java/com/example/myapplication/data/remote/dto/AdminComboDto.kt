package com.example.myapplication.data.remote.dto

data class AdminComboDto(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isActive: Boolean
)

// 2. Enum định nghĩa 2 Tab Trạng thái
enum class ComboStatusTab(val title: String, val isActiveValue: Boolean) {
    ACTIVE("Đang bán", true),
    INACTIVE("Ngừng bán", false)
}

data class AdminComboUpdateRequest(
    val name: String,
    val description: String?,
    val price: Double,
    val isActive: Boolean
)


data class AdminComboCreateRequest(
    val name: String,
    val description: String?,
    val price: Double,
    val isActive: Boolean
)

