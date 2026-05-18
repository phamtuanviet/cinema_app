package com.example.myapplication.presentation.component

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.material3.*

@Composable
fun BottomBarSelection(
    totalPrice: Double,
    selectedSeats: List<String>,
    onContinueClick: (sessionId: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // 1. XÓA .background(Color.White)
            // Vì ở màn hình chính ta đã bọc BottomBar này trong Surface của MD3,
            // nền sẽ tự động đồng bộ (trắng ở Light Mode, xám đậm ở Dark Mode).
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                text = "Ghế đã chọn",
                style = MaterialTheme.typography.bodySmall,
                // Dùng onSurfaceVariant cho các text phụ (tiêu đề nhỏ) để tạo độ tương phản nhẹ nhàng
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = selectedSeats.joinToString(", "),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                // Dùng onSurface cho text chính
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tổng tiền",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "${totalPrice.toInt()} đ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                // 2. THAY THẾ Color(0xFF1976D2) BẰNG MÀU PRIMARY
                // Tự động sử dụng màu nhấn (Primary) của theme hiện tại
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = {
                // Logic được giữ nguyên hoàn toàn
                Log.d("BottomBarSelection", "onContinueClick: $selectedSeats")
                if (selectedSeats.isNotEmpty()) {
                    Log.d("BottomBarSelection", "onContinueClick: $selectedSeats")
                    onContinueClick("Hi")
                }
            },
            shape = RoundedCornerShape(12.dp),
            // Khai báo rõ màu sắc của nút theo chuẩn MD3
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Tiếp tục",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}