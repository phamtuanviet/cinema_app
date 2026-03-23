package com.example.myapplication.presentation.component


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


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun VoucherSection(
    vouchers: List<VoucherDto>,
    selectedVoucher: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Nếu danh sách trống thì không hiển thị khối này
    if (vouchers.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 🎟 Tiêu đề
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(text = "🎟️", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PHƯƠNG THỨC GIẢM GIÁ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Danh sách Voucher
            vouchers.forEachIndexed { index, voucher ->
                val isSelected = selectedVoucher == voucher.id

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        // Nếu đang chọn thì đổi màu nền nhẹ để highlight
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else Color.Transparent
                        )
                        .clickable {
                            // Logic UX: Click lại vào cái đang chọn thì sẽ bỏ chọn
                            onSelect(if (isSelected) null else voucher.id)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelect(if (isSelected) null else voucher.id) }
                    )

                    Spacer(Modifier.width(8.dp))

                    Column {
                        Text(
                            text = voucher.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )

                        voucher.description?.let {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Đường kẻ ngang giữa các voucher
                if (index < vouchers.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}