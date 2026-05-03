package com.example.myapplication.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun canRefundTicket(showtimeStartStr: String?, status: String?): Boolean {
    // Nếu vé đã hủy hoặc hoàn rồi thì không hiện nút nữa
    if (showtimeStartStr == null ||
        status.equals("REFUNDED", ignoreCase = true) ||
        status.equals("CANCELED", ignoreCase = true)) {
        return false
    }

    return try {
        // Parse thời gian chuỗi thành đối tượng LocalDateTime
        // (Giả định backend Spring Boot gửi về định dạng chuẩn ISO: "2026-05-03T18:00:00")
        val showtimeDate = LocalDateTime.parse(showtimeStartStr, DateTimeFormatter.ISO_DATE_TIME)
        val now = LocalDateTime.now()

        // Tính khoảng cách thời gian (phút)
        val minutesUntilShowtime = ChronoUnit.MINUTES.between(now, showtimeDate)

        // Phải còn hơn hoặc bằng 120 phút (2 tiếng) mới được hoàn
        minutesUntilShowtime >= 120
    } catch (e: Exception) {
        // Nếu parse lỗi (backend gửi sai format), ẩn nút đi cho an toàn
        false
    }
}