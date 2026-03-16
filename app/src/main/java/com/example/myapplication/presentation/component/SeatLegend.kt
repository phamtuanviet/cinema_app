package com.example.myapplication.presentation.component

import LegendItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SeatLegend() {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            LegendItem(Color.LightGray, "Ghế trống")
            LegendItem(Color.Blue, "Ghế đang giữ")
            LegendItem(Color(0xFF1E88E5), "Ghế đang chọn")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            LegendItem(Color.Red, "Ghế đã bán")
            LegendItem(Color.Yellow, "Ghế đặt trước")
        }
    }
}