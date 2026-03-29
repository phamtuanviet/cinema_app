package com.example.myapplication.presentation.component

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
import com.example.myapplication.data.remote.enums.VoucherStatus


@Composable
fun VoucherTab(
    state: VoucherListState,
    viewModel: VoucherListViewModel
) {
    // focus manager
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // không hiển thị ripple
            ) {
                focusManager.clearFocus() // ẩn bàn phím
            }
    ) {

        // --- SUB TAB ---
        val voucherTabIndex = when (state.selectedVoucherTab) {
            VoucherStatus.AVAILABLE  -> 0
            VoucherStatus.USED -> 1
            else -> 2
        }

        TabRow(selectedTabIndex = voucherTabIndex) {
            Tab(
                selected = state.selectedVoucherTab == VoucherStatus.AVAILABLE,
                onClick = { viewModel.onVoucherTabChange(VoucherStatus.AVAILABLE) },
                text = { Text("Available") }
            )
            Tab(
                selected = state.selectedVoucherTab == VoucherStatus.USED,
                onClick = { viewModel.onVoucherTabChange(VoucherStatus.USED) },
                text = { Text("Used") }
            )
            Tab(
                selected = state.selectedVoucherTab == VoucherStatus.EXPIRED,
                onClick = { viewModel.onVoucherTabChange(VoucherStatus.EXPIRED) },
                text = { Text("Expired") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- ADD VOUCHER ---
        var code by remember { mutableStateOf("") }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = code,
                onValueChange = { code = it },
                placeholder = { Text("Nhập mã voucher") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.addVoucher(code) },
                enabled = !state.isAddingVoucher
            ) {
                if (state.isAddingVoucher) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Add")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- VOUCHER LIST ---
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.vouchers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có voucher nào")
            }
        } else {
            LazyColumn {
                items(state.vouchers) { voucher ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(voucher.code, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = state.selectedVoucherTab.displayName(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Giảm: ${voucher.discountValue}${if (voucher.discountType=="percent") "%" else "đ"}")
                            voucher.minOrderValue?.let { Text("Đơn tối thiểu: $it") }
                            voucher.maxDiscount?.let { Text("Giảm tối đa: $it") }

                            voucher.movieTitle?.let { Text("Phim: $it") }
                            voucher.cinemaName?.let { Text("Rạp: $it") }
                            voucher.roomName?.let { Text("Phòng: $it") }
                            voucher.showtime?.let { Text("Suất chiếu: $it") }

                            voucher.expiryDate?.let { Text("HSD: $it") }
                            voucher.usedAt?.let { Text("Đã dùng: $it") }
                        }
                    }
                }
            }
        }
    }
}