//package com.example.myapplication.presentation.component
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.myapplication.data.remote.dto.SeatDto
//import com.example.myapplication.data.remote.enums.SeatStatus
//
//@Composable
//fun SeatItem(
//
//    seat: SeatDto,
//    isSelected: Boolean,
//    onClick: () -> Unit
//
//) {
//
//    val color = when {
//
//        isSelected -> Color.DarkGray
//
//        seat.status == SeatStatus.COMPLETED -> Color.Red
//
//        seat.status == SeatStatus.HOLD -> Color.Blue
//
//        else -> Color.LightGray
//    }
//
//    Box(
//        modifier = Modifier
//            .padding(4.dp)
//            .size(32.dp)
//            .clip(RoundedCornerShape(6.dp))
//            .background(color)
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//
//        Text(
//            text = seat.seatNumber.toString(),
//            fontSize = 12.sp
//        )
//    }
//}