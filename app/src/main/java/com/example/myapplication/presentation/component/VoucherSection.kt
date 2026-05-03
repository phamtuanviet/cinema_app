package com.example.myapplication.presentation.component


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text

import androidx.compose.ui.unit.dp

import com.example.myapplication.data.remote.dto.VoucherDto


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.remote.enums.VoucherDiscountType
import java.math.BigDecimal

@Composable
fun VoucherSection(
    vouchers: List<VoucherDto>,
    selectedVoucherId: String?,
    currentSubtotal: Double,
    onSelect: (String?) -> Unit
) {
    if (vouchers.isEmpty()) return

    Column {
        Text(
            text = "Khuyến mãi",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = vouchers,
                key = { it.id } // 👈 Thêm 'key' giúp Compose track trạng thái item tốt hơn khi danh sách không đổi
            ) { voucher ->
                val isSelected = voucher.id == selectedVoucherId

                // 👈 Truyền thẳng voucher và currentSubtotal vào VoucherCard
                VoucherCard(
                    voucher = voucher,
                    isSelected = isSelected,
                    currentSubtotal = currentSubtotal, // Truyền trực tiếp
                    onClick = {
                        if (isSelected) {
                            onSelect(null)
                        } else {
                            onSelect(voucher.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun VoucherCard(
    voucher: VoucherDto,
    isSelected: Boolean,
    currentSubtotal: Double, // 👈 Nhận subtotal làm tham số
    onClick: () -> Unit
) {
    // 👈 Đưa logic tính isEligible vào TRONG component này
    // Bất cứ khi nào currentSubtotal đổi, Compose sẽ gọi lại đoạn này
    val minAmount = voucher.minOrderAmount?.toDouble() ?: 0.0
    val isEligible = currentSubtotal >= minAmount && voucher.isUsable == true

    val alpha = if (isEligible) 1f else 0.4f
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface

    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .width(260.dp)
            .alpha(alpha)
            .clickable(enabled = isEligible, onClick = onClick) // Chỉ cho click nếu đủ điều kiện
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = voucher.code,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val discountText = when (voucher.discountType) {
                VoucherDiscountType.PERCENT -> "Giảm ${voucher.discountValue}%"
                VoucherDiscountType.FIXED -> "Giảm ${voucher.discountValue.toInt()}đ"
                null -> "Mã giảm giá"
            }
            Text(
                text = "$discountText ${if (voucher.maxDiscount != null) "(Tối đa ${voucher.maxDiscount.toInt()}đ)" else ""}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            if (voucher.minOrderAmount != null && voucher.minOrderAmount > BigDecimal.ZERO) {
                Text(
                    text = "Cho đơn từ ${voucher.minOrderAmount.toInt()}đ",
                    fontSize = 12.sp,
                    color = if (isEligible) Color.Gray else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (!isEligible) {
                Text(
                    text = "Chưa đủ điều kiện",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Text(
                text = "HSD: ${voucher.expiryDate}",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}