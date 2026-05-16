package com.example.myapplication.data.remote.dto

data class AdminBookingDto(
    val id: String,
    val ticketCode: String,
    val userEmail: String,
    val movieName: String,
    val showtimeTime: String, // Ví dụ: 19:30 - 16/05/2026
    val totalAmount: Double,
    val status: String
)

// 2. Enum định nghĩa 4 Tab
enum class BookingStatusTab(val title: String, val statusValue: String) {
    PENDING("Chờ TT", "PENDING"),
    PAID("Đã TT", "PAID"),
    CANCELLED("Đã Hủy", "CANCELLED"),
    REFUNDED("Đã Hoàn", "REFUNDED")
}

data class AdminBookingDetailDto(
    val id: String,
    val ticketCode: String,
    val qrCodeUrl: String?,
    val status: String,
    val createdAt: String, // Định dạng: HH:mm - dd/MM/yyyy
    val cancelledAt: String?,

    // Khách hàng
    val userName: String,
    val userEmail: String,
    val userPhone: String?,

    // Phim & Suất chiếu
    val movieName: String,
    val moviePosterUrl: String?,
    val cinemaName: String,
    val roomName: String,
    val showtimeTime: String,

    // Chi tiết dịch vụ
    val seats: String, // Ví dụ: "A1, A2, A3"
    val combos: String, // Ví dụ: "2x Popcorn, 1x Pepsi"

    // Chi tiết thanh toán
    val seatAmount: Double,
    val comboAmount: Double,
    val voucherDiscount: Double,
    val pointDiscount: Double,
    val totalAmount: Double
)