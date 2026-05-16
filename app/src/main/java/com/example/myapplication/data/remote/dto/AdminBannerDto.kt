package com.example.myapplication.data.remote.dto

data class AdminBannerDto(
    val id: String,
    val imageUrl: String,
    val actionType: String, // "MOVIE" hoặc "URL"
    val targetUrl: String?,
    val movieId: String?,
    val movieName: String?, // Bổ sung để hiển thị tên phim lên UI
    val priority: Int,
    val isActive: Boolean
)

// 2. Enum định nghĩa 2 Tab
enum class BannerActionTab(val title: String, val typeValue: String) {
    MOVIE("Phim nổi bật", "MOVIE"),
    URL("Đường dẫn (URL)", "URL")
}


data class AdminBannerUpdateRequest(
    val actionType: String,
    val targetUrl: String?,
    val movieId: String?,
    val priority: Int,
    val isActive: Boolean
)

// 2. DTO Lấy danh sách phim hiển thị trong Dropdown
data class AdminMovieSimpleDto(
    val id: String,
    val title: String
)

data class AdminBannerCreateRequest(
    val actionType: String,
    val targetUrl: String?,
    val movieId: String?,
    val priority: Int,
    val isActive: Boolean
)