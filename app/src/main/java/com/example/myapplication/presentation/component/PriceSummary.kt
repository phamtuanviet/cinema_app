package com.example.myapplication.presentation.component




import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.example.myapplication.presentation.screen.booking.other_options.BookingOtherOptionsState

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PriceSummary(
    state: BookingOtherOptionsState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 🧾 Tiêu đề
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "CHI TIẾT THANH TOÁN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val subTotal = state.seatAmount + state.comboAmount
            val totalDiscount = state.voucherDiscount + state.pointDiscount

            // Tổng tiền gốc
            PriceRow(
                label = "Tổng tiền (Vé + Bắp nước)",
                amount = subTotal
            )

            // Chỉ hiện dòng giảm giá nếu có áp dụng (điểm hoặc voucher)
            if (totalDiscount > 0) {
                Spacer(Modifier.height(8.dp))
                PriceRow(
                    label = "Số tiền được giảm",
                    amount = totalDiscount,
                    isDiscount = true
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 💵 Tổng tiền cần thanh toán
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cần thanh toán",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${state.totalAmount.toInt()}đ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    // Dùng màu đỏ/màu nhấn để nổi bật tổng tiền cuối cùng
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

