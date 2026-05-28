package com.example.myapplication.utils
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.DayOfWeek
import java.time.ZoneId
import java.util.Locale

fun formatShowtime(isoString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        dateTime.format(formatter)
    } catch (e: Exception) {
        // Fallback an toàn nếu API trả về sai format
        isoString.substringAfter("T").take(5)
    }
}

// Hàm 2: Cắt chuỗi "2026-05-29" thành "29/05" cho thanh chọn ngày
fun formatDateDisplay(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}

fun formatTicketTime(isoString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(isoString.take(19))
        val formatter = DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy")
        dateTime.format(formatter)
    } catch (e: Exception) {
        isoString
    }
}

// Hàm 2: Xóa đuôi .0 và thêm phẩy hàng nghìn cho tiền tệ (Ví dụ: 65,000)
fun formatPrice(amount: Double): String {
    return "%,d".format(amount.toLong())
}

fun formatPrice(amount: BigDecimal): String {
    return "%,d".format(amount.toLong())
}

fun getFormattedDateInfo(dateString: String): Pair<String, String> {
    return try {
        // Cắt lấy 10 ký tự đầu (đề phòng API trả về ISO "2026-05-29T08:00")
        val parsedDate = LocalDate.parse(dateString.take(10))
        val today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))

        // 1. Xử lý dòng trên: "Hôm nay", "Ngày mai" hoặc "Thứ ..."
        val topText = when (parsedDate) {
            today -> "Hôm nay"
            today.plusDays(1) -> "Ngày mai"
            else -> when (parsedDate.dayOfWeek) {
                DayOfWeek.MONDAY -> "Thứ 2"
                DayOfWeek.TUESDAY -> "Thứ 3"
                DayOfWeek.WEDNESDAY -> "Thứ 4"
                DayOfWeek.THURSDAY -> "Thứ 5"
                DayOfWeek.FRIDAY -> "Thứ 6"
                DayOfWeek.SATURDAY -> "Thứ 7"
                DayOfWeek.SUNDAY -> "Chủ nhật"
                else -> ""
            }
        }

        // 2. Xử lý dòng dưới: "28/05" hoặc có thể sửa thành "28 Th 5" nếu bạn thích
        val bottomText = parsedDate.format(DateTimeFormatter.ofPattern("dd/MM"))

        Pair(topText, bottomText)
    } catch (e: Exception) {
        // Fallback an toàn nếu API lỗi
        Pair("Ngày", dateString.take(5))
    }
}

fun formatTransactionDate(isoString: String): String {
    return try {
        // Cắt lấy tối đa 19 ký tự đầu (yyyy-MM-ddTHH:mm:ss) để tránh lỗi parse timezone
        val dateTime = LocalDateTime.parse(isoString.take(19))
        val formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")
        dateTime.format(formatter)
    } catch (e: Exception) {
        isoString // Fallback an toàn
    }
}

fun localizeTransactionDescription(rawDescription: String): String {
    val desc = rawDescription.trim()

    return when {
        desc.startsWith("Use points for booking") -> {
            val ticketCode = desc.substringAfter("Use points for booking").trim()
            "Đổi điểm thanh toán cho mã vé $ticketCode"
        }

        desc.startsWith("Refund points for expired booking") -> {
            val ticketCode = desc.substringAfter("Refund points for expired booking").trim()
            "Hoàn điểm do hết hạn thanh toán mã vé $ticketCode"
        }

        // 3. Trường hợp: Refund points for canceled booking {ticketCode}
        desc.startsWith("Refund points for canceled booking") -> {
            val ticketCode = desc.substringAfter("Refund points for canceled booking").trim()
            "Hoàn điểm do hủy mã vé $ticketCode"
        }

        desc.startsWith("Earned points from booking") -> {
            val ticketCode = desc.substringAfter("Earned points from booking").trim()
            "Điểm kiếm được cho mã vé $ticketCode"
        }

        // 4. Trường hợp: Refund point for failed booking (Không có mã vé)
        desc.contains("Refund point for failed booking", ignoreCase = true) -> {
            "Hoàn điểm do giao dịch đặt vé thất bại"
        }

        else -> desc
    }
}

fun formatExpiryDate(isoString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(isoString.take(19))
        val formatter = DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy")
        dateTime.format(formatter)
    } catch (e: Exception) {
        isoString
    }
}

fun formatChatbotTime(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "Đang cập nhật"
    return try {
        val dateTime = LocalDateTime.parse(isoString.take(19))
        val formatter = DateTimeFormatter.ofPattern("HH:mm • dd/MM")
        dateTime.format(formatter)
    } catch (e: Exception) {
        isoString
    }
}

// Format giá tiền: 65000.0 -> 65,000đ
fun formatChatbotPrice(price: Double?): String {
    if (price == null || price == 0.0) return "Đang cập nhật"
    return "%,dđ".format(Locale("vi", "VN"), price.toLong())
}