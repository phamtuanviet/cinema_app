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
    // Map đúng 4 màu từ SeatColors (Đã đồng bộ Sáng/Tối)
    val seatColor = when (status) {
        SeatStatus.AVAILABLE -> SeatColors.available
        SeatStatus.HOLD_BY_ME -> SeatColors.selected
        SeatStatus.HOLD_BY_OTHER -> SeatColors.heldByOther
        SeatStatus.BOOKED -> SeatColors.booked
    }

    SeatItemShape(
        color = seatColor,
        modifier = Modifier.width(32.dp),
        onClick = {
            if (status == SeatStatus.AVAILABLE || status == SeatStatus.HOLD_BY_ME) {
                onClick()
            }
        }
    )
}

@Composable
fun CoupleSeatItem(status1: SeatStatus, status2: SeatStatus, onClick: () -> Unit) {
    val isBooked = status1 == SeatStatus.BOOKED || status2 == SeatStatus.BOOKED
    val isSelected = status1 == SeatStatus.HOLD_BY_ME || status2 == SeatStatus.HOLD_BY_ME
    val isHeldByOther = status1 == SeatStatus.HOLD_BY_OTHER || status2 == SeatStatus.HOLD_BY_OTHER

    val seatColor = when {
        isBooked -> SeatColors.booked
        isSelected -> SeatColors.selected
        isHeldByOther -> SeatColors.heldByOther
        else -> SeatColors.available
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