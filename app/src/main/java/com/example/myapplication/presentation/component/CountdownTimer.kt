package com.example.myapplication.presentation.component
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.Instant

import androidx.compose.material3.*

@Composable
fun CountdownTimer(
    expiresAt: String?, // Nhận trực tiếp String thay vì Long
    onExpire: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(0L) }

    // ==============================
    // 1. LOGIC TÍNH TOÁN (Giữ nguyên hoàn toàn)
    // ==============================
    LaunchedEffect(expiresAt) {
        if (expiresAt.isNullOrBlank()) return@LaunchedEffect

        // Parse chuỗi ISO 8601 (UTC) thành mili-giây
        val expireTimeMillis = try {
            Instant.parse(expiresAt).toEpochMilli()
        } catch (e: Exception) {
            0L // Nếu lỗi parse (chuỗi sai định dạng), gán bằng 0
        }

        if (expireTimeMillis <= 0L) return@LaunchedEffect

        while (true) {
            val now = System.currentTimeMillis()
            val remaining = expireTimeMillis - now

            if (remaining <= 0) {
                timeLeft = 0
                onExpire()
                break
            } else {
                timeLeft = remaining
            }
            delay(1000)
        }
    }

    // Tính toán phút và giây từ timeLeft
    val totalSeconds = timeLeft / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60

    // ==============================
    // 2. GIAO DIỆN (Chuẩn Material Design 3)
    // ==============================
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp) // Thêm chút padding dọc để Timer không bị dính sát vào nội dung khác
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = "Time remaining",
            // Sử dụng màu error (đỏ) hoặc primary. Ở đây dùng colorScheme.error
            // để nhấn mạnh đây là thời gian đếm ngược (cảnh báo).
            // Nếu bạn muốn màu nhẹ nhàng hơn, có thể đổi thành MaterialTheme.colorScheme.primary
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            // Sử dụng Typography chuẩn của MD3
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            // Đồng bộ màu sắc chữ với Icon
            color = MaterialTheme.colorScheme.error
        )
    }
}