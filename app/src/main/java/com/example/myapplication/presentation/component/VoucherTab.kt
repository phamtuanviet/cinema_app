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
        // --- SUB TAB ---
        val voucherTabIndex = when (state.selectedVoucherTab) {
            VoucherStatus.AVAILABLE -> 0
            VoucherStatus.USED -> 1
            else -> 2
        }

        SecondaryTabRow(selectedTabIndex = voucherTabIndex) {
            val tabs = listOf(
                VoucherStatus.AVAILABLE to "Khả dụng",
                VoucherStatus.USED to "Đã dùng",
                VoucherStatus.EXPIRED to "Hết hạn"
            )

            tabs.forEachIndexed { index, (status, title) ->
                Tab(
                    selected = voucherTabIndex == index,
                    onClick = { viewModel.onVoucherTabChange(status) },
                    text = { Text(title) }
                )
            }
        }

        // --- ADD VOUCHER ---
        var code by remember { mutableStateOf("") }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase() }, // Code thường viết hoa
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
                modifier = Modifier.height(56.dp) // Đồng bộ chiều cao với TextField
            ) {
                if (state.isAddingVoucher) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Thêm", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Hiển thị lỗi nếu có
        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall
            )
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Phần bên trái (Discount Info)
            Box(
                modifier = Modifier
                    .weight(0.35f)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxHeight()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Rounded.ConfirmationNumber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${voucher.discountValue}${if (voucher.discountType == "percent") "%" else "đ"}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "GIẢM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Đường nét đứt chia cắt (Mô phỏng)
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.Gray.copy(alpha = 0.3f))
            )

            // Phần bên phải (Chi tiết)
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = voucher.code,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                voucher.minOrderValue?.let {
                    Text(
                        text = "Đơn tối thiểu: $it đ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                voucher.maxDiscount?.let {
                    Text(
                        text = "Giảm tối đa: $it đ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (voucher.movieTitle != null || voucher.cinemaName != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    voucher.movieTitle?.let { Text("Phim: $it", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    voucher.cinemaName?.let { Text("Rạp: $it", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = voucher.expiryDate?.let { "HSD: $it" } ?: "Không thời hạn",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}