package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarSelection(
    totalPrice: Double,
    selectedSeats: List<String>,
    onContinueClick: (List<String>) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {

            Text(
                text = "Ghế đã chọn",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = selectedSeats.joinToString(","),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tổng tiền",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "${totalPrice.toInt()} đ",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
        }

        Button(
            onClick = {
                if (selectedSeats.isNotEmpty()) {
                    onContinueClick(selectedSeats)
                }
            },
            shape = RoundedCornerShape(12.dp)
        ) {

            Text("Tiếp tục")
        }
    }
}