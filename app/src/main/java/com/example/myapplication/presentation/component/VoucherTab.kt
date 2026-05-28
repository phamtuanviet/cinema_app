package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text

import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.Discount
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.myapplication.presentation.screen.voucher.voucher_list.VoucherListState
import com.example.myapplication.presentation.screen.voucher.voucher_list.VoucherListViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.myapplication.data.remote.dto.UserVoucherResponse
import com.example.myapplication.data.remote.enums.VoucherStatus
import com.example.myapplication.utils.formatExpiryDate
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherTab(
    state: VoucherListState,
    viewModel: VoucherListViewModel
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        // --- SUB TAB ĐƯỢC LÀM MỚI ---
        val voucherTabIndex = when (state.selectedVoucherTab) {
            VoucherStatus.AVAILABLE -> 0
            VoucherStatus.USED -> 1
            else -> 2
        }

        TabRow(
            selectedTabIndex = voucherTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            divider = {
                // Làm đường kẻ mờ đi cho thanh thoát
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            },
            indicator = { tabPositions ->
                if (voucherTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[voucherTabIndex]),
                        color = MaterialTheme.colorScheme.primary,
                        height = 3.dp // Đường kẻ dưới tab dày dặn hơn một chút
                    )
                }
            }
        ) {
            val tabs = listOf(
                VoucherStatus.AVAILABLE to "Khả dụng",
                VoucherStatus.USED to "Đã dùng",
                VoucherStatus.EXPIRED to "Hết hạn"
            )

            tabs.forEachIndexed { index, (status, title) ->
                val isSelected = voucherTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.onVoucherTabChange(status) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium, // 🔥 TĂNG CỠ CHỮ
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f) // Màu chữ khi không chọn
                )
            }
        }

        // --- ADD VOUCHER (Giữ nguyên) ---
        var code by remember { mutableStateOf("") }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase() },
                placeholder = { Text("Nhập mã ưu đãi...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Discount, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.addVoucher(code)
                    code = ""
                },
                enabled = !state.isAddingVoucher && code.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(56.dp)
            ) {
                if (state.isAddingVoucher) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Thêm", fontWeight = FontWeight.Bold)
                }
            }
        }


        // --- VOUCHER LIST ---
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.vouchers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Không có mã giảm giá nào ở mục này.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.vouchers) { voucher ->
                    VoucherItemCard(voucher)
                }
            }
        }
    }
}

@Composable
fun VoucherItemCard(voucher: UserVoucherResponse) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // --- Phần bên trái (Discount Info - Giữ nguyên) ---
            Box(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Rounded.ConfirmationNumber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatDiscountText(voucher.discountValue, voucher.discountType),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Visible
                    )
                    Text(
                        text = "GIẢM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.Gray.copy(alpha = 0.2f))
            )

            // --- Phần bên phải (Chi tiết) ---
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = voucher.code,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                voucher.minOrderValue?.let {
                    Text(
                        text = "Đơn tối thiểu: ${formatMoney(it)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                voucher.maxDiscount?.let {
                    Text(
                        text = "Giảm tối đa: ${formatMoney(it)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (voucher.movieTitle != null || voucher.cinemaName != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    voucher.movieTitle?.let { Text("Phim: $it", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    voucher.cinemaName?.let { Text("Rạp: $it", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 🔥 THAY ĐỔI MÀU SẮC VÀ FORMAT HSD Ở ĐÂY
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp), // Tăng size nhẹ lên cho rõ
                        tint = MaterialTheme.colorScheme.onSurfaceVariant // Đổi sang xám nhạt tinh tế
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = voucher.expiryDate?.let { "HSD: ${formatExpiryDate(it)}" } ?: "Không thời hạn",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Đổi sang xám nhạt tinh tế
                    )
                }
            }
        }
    }
}
// --- HÀM HỖ TRỢ FORMAT SỐ ---

// Format số tiền lớn thành chữ dễ nhìn (VD: 20000.0 -> 20K)
fun formatDiscountText(value: Double, type: String): String {
    return if (type == "PERCENT") {
        // Bỏ đuôi .0 nếu là số nguyên (VD: 15.0 -> 15)
        val formatted = if (value % 1 == 0.0) value.toInt().toString() else value.toString()
        "$formatted%"
    } else {
        // Với tiền mặt, nếu từ 1000 trở lên thì chuyển thành K cho ngắn
        if (value >= 1000) {
            val kValue = value / 1000
            val formatted = if (kValue % 1 == 0.0) kValue.toInt().toString() else kValue.toString()
            "${formatted}K"
        } else {
            val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            "${format.format(value)}đ"
        }
    }
}

// Format số tiền bình thường có dấu chấm (VD: 100000.0 -> 100.000 đ)
fun formatMoney(value: Double): String {
    val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${format.format(value)} đ"
}