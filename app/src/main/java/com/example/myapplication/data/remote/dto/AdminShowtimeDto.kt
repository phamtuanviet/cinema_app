package com.example.myapplication.data.remote.dto

data class AdminShowtimeDto(
    val id: String,
    val movieName: String,
    val moviePosterUrl: String?, // Thêm ảnh cho đẹp UI
    val cinemaName: String,
    val roomName: String,
    val startTime: String, // Format: ISO-8601 (VD: 2026-05-12T19:00:00)
    val endTime: String,
    val basePrice: Double,
    val status: String
)

// 2. Enum định nghĩa 3 Tab
enum class ShowtimeTab(val title: String, val filterValue: String) {
    UPCOMING("Sắp chiếu", "UPCOMING"),
    ONGOING("Đang chiếu", "ONGOING"),
    PAST("Đã chiếu", "PAST")
}


data class SimpleItemDto(
    val id: String,
    val name: String // Dùng chung cho Phim (Title), Rạp (Name), Phòng (Name)
)

data class AdminShowtimeCreateRequest(
    val movieId: String,
    val roomId: String,
    val startTime: String, // Format: yyyy-MM-dd'T'HH:mm:ss
    val endTime: String,
    val basePrice: Double,
    val weekendModifier: Double
)

data class AdminShowtimeUpdateRequest(
    val startTime: String,
    val endTime: String,
    val status: String
)