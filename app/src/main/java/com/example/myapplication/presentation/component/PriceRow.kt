package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.math.BigDecimal

@Composable
fun PriceRow(
    label: String,
    value: BigDecimal,
    highlight: Boolean = false
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(label)

        Text(
            "$value đ",
            color = if (highlight) Color.Red else Color.Black,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
    }
}