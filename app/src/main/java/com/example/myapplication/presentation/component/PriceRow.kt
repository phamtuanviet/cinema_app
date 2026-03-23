package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PriceRow(
    label: String,
    amount: Double,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (isDiscount) "-${amount.toInt()}đ" else "${amount.toInt()}đ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isDiscount) FontWeight.SemiBold else FontWeight.Medium,
            // Nếu là giảm giá thì dùng màu xanh lam/xanh lá (tertiary), ngược lại dùng màu text thường
            color = if (isDiscount) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
        )
    }
}