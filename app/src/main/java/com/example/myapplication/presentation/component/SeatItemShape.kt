package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.enums.SeatStatus

// Định nghĩa màu sắc cho gọn
val ColorAvailable = Color(0xFFE0E0E0)   // Xám nhạt (Trống)
val ColorSelected = Color(0xFFE50914)    // Đỏ (Bạn đang chọn)
val ColorHeldByOther = Color(0xFFFF9800) // Cam (Người khác đang chọn/giữ)
val ColorBooked = Color(0xFF424242)

@Composable
fun SeatItemShape(
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(color)
            .clickable { onClick() }
    )
}

@Composable
fun SingleSeatItem(status: SeatStatus, onClick: () -> Unit) {
    // 2. Map đúng 4 màu tương ứng với 4 Enum
    val seatColor = when (status) {
        SeatStatus.AVAILABLE -> ColorAvailable
        SeatStatus.HOLD_BY_ME -> ColorSelected
        SeatStatus.HOLD_BY_OTHER -> ColorHeldByOther
        SeatStatus.BOOKED -> ColorBooked
    }

    SeatItemShape(
        color = seatColor,
        modifier = Modifier.width(32.dp),
        onClick = {
            // Chỉ cho phép click nếu ghế trống (hoặc bạn muốn cho phép bỏ chọn ghế HOLD_BY_ME)
            if (status == SeatStatus.AVAILABLE || status == SeatStatus.HOLD_BY_ME) {
                onClick()
            }
        }
    )
}

@Composable
fun CoupleSeatItem(status1: SeatStatus, status2: SeatStatus, onClick: () -> Unit) {
    // 3. Xử lý logic ưu tiên màu cho ghế đôi
    // Ưu tiên: Đã bán > Mình đang chọn > Người khác đang chọn > Trống
    val isBooked = status1 == SeatStatus.BOOKED || status2 == SeatStatus.BOOKED
    val isSelected = status1 == SeatStatus.HOLD_BY_ME || status2 == SeatStatus.HOLD_BY_ME
    val isHeldByOther = status1 == SeatStatus.HOLD_BY_OTHER || status2 == SeatStatus.HOLD_BY_OTHER

    val seatColor = when {
        isBooked -> ColorBooked
        isSelected -> ColorSelected
        isHeldByOther -> ColorHeldByOther
        else -> ColorAvailable
    }

    SeatItemShape(
        color = seatColor,
        modifier = Modifier.width(72.dp),
        onClick = {
            if (!isBooked && !isHeldByOther) {
                onClick()
            }
        }
    )
}