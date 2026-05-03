package com.example.myapplication.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.dto.UserVoucherChatbotResponse

@Composable
fun VoucherListWidget(vouchers: List<UserVoucherChatbotResponse>, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bạn đang có ${vouchers.size} Voucher", fontWeight = FontWeight.Bold)
            }
            if (vouchers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                // Hiển thị nhanh voucher đầu tiên làm mồi nhử
                Text(
                    text = "🎁 ${vouchers.first().displayName}",
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                "Mở kho Voucher ngay",
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}