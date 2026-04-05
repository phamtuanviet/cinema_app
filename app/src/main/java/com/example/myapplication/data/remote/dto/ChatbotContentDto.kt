package com.example.myapplication.data.remote.dto

// 1. Lịch sử đặt vé
data class BookingHistoryResponse(
    val ticketCode: String?,
    val movieTitle: String?,
    val cinemaName: String?,
    val roomName: String?,
    val startTime: String?,
    val seats: String?,
    val totalAmount: Double?,
    val statusDisplay: String?,
    val qrCodeUrl: String?
)

// 2. Voucher của người dùng
data class UserVoucherChatbotResponse(
    val voucherCode: String?,
    val displayName: String?,
    val description: String?,
    val expiryDate: String?,
    val status: String?,
    val minOrderValue: Double?
)

// 3. Điểm thưởng
data class UserPointsResponse(
    val availablePoints: Int?,
    val monetaryValueFormatted: String?,
    val accumulationPolicy: String?,
    val note: String?
)

// 4. Thông tin phim cơ bản (Dùng cho danh sách tìm kiếm/đang chiếu)
data class MovieChatbotResponse(
    val movieId: String?,
    val title: String?,
    val posterUrl: String?,
    val genres: List<String>?,
    val durationMinutes: Int?,
    val ageRating: String?,
    val averageRating: Double?
)

// 5. Chi tiết một bộ phim
data class MovieDetailResponse(
    val movieId: String?,
    val title: String?,
    val description: String?,
    val posterUrl: String?,
    val trailerUrl: String?,
    val genres: List<String>?,
    val durationMinutes: Int?,
    val ageRating: String?,
    val language: String?,
    val releaseDate: String?,
    val averageRating: Double?,
    val ratingCount: Int?
)

// 6. Lịch chiếu phim
data class ShowtimeChatbotResponse(
    val showtimeId: String?,
    val movieTitle: String?,
    val cinemaName: String?,
    val cinemaAddress: String?,
    val roomName: String?,
    val startTime: String?,
    val price: Double?
)

// 7. Rạp chiếu phim gần đây
data class CinemaNearbyResponse(
    val id: String?,
    val name: String?,
    val address: String?,
    val cineplex: String?,
    val distance: Double?,
    val logoUrl: String?
)