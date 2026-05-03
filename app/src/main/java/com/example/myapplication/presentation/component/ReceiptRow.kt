package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs
@Composable
fun ReceiptRow(label: String, value: Double, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "${if (isDiscount) "-" else ""}${abs(value)}đ",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDiscount) Color.Red else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isDiscount) FontWeight.Bold else FontWeight.Normal
        )
    }
}