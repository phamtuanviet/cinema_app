package com.example.myapplication.presentation.screen.admin.voucher.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVoucherEditScreen(
    voucherId: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminVoucherEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa Voucher", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        bottomBar = {
            if (!state.isLoadingData) {
                Button(
                    onClick = { viewModel.onEvent(VoucherEditEvent.SaveClicked) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else Text("Lưu thay đổi", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            when {
                state.isLoadingData -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.error != null) {
                            Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp)) {
                                Text(text = state.error!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp))
                            }
                        }

                        // --- MÃ CODE & TRẠNG THÁI ---
                        OutlinedTextField(
                            value = state.code,
                            onValueChange = { viewModel.onEvent(VoucherEditEvent.CodeChanged(it)) },
                            label = { Text("Mã Voucher (CODE) (*)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            singleLine = true
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Trạng thái Voucher", fontWeight = FontWeight.Bold)
                                    Text(if (state.isActive) "Đang bật" else "Đã tắt", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = state.isActive,
                                    onCheckedChange = { viewModel.onEvent(VoucherEditEvent.IsActiveChanged(it)) }
                                )
                            }
                        }

                        // --- LOẠI GIẢM GIÁ ---
                        Text("Loại giảm giá", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SelectableChip(
                                text = "Phần trăm (%)",
                                isSelected = state.discountType == "PERCENT",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.onEvent(VoucherEditEvent.TypeChanged("PERCENT")) }
                            )
                            SelectableChip(
                                text = "Tiền mặt (VNĐ)",
                                isSelected = state.discountType == "FIXED",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.onEvent(VoucherEditEvent.TypeChanged("FIXED")) }
                            )
                        }

                        // --- GIÁ TRỊ GIẢM ---
                        OutlinedTextField(
                            value = state.discountValueStr,
                            onValueChange = { viewModel.onEvent(VoucherEditEvent.ValueChanged(it)) },
                            label = { Text(if (state.discountType == "PERCENT") "Mức giảm (%) (*)" else "Mức giảm (VNĐ) (*)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // NẾU LÀ % -> Hiện Max Discount
                        if (state.discountType == "PERCENT") {
                            OutlinedTextField(
                                value = state.maxDiscountStr,
                                onValueChange = { viewModel.onEvent(VoucherEditEvent.MaxDiscountChanged(it)) },
                                label = { Text("Giảm tối đa (VNĐ) - Tùy chọn") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        // --- ĐIỀU KIỆN ÁP DỤNG ---
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Điều kiện & Giới hạn", fontWeight = FontWeight.SemiBold)

                        OutlinedTextField(
                            value = state.minOrderValueStr,
                            onValueChange = { viewModel.onEvent(VoucherEditEvent.MinOrderChanged(it)) },
                            label = { Text("Đơn tối thiểu (VNĐ) - Tùy chọn") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = state.usageLimitStr,
                                onValueChange = { viewModel.onEvent(VoucherEditEvent.UsageLimitChanged(it)) },
                                label = { Text("Giới hạn số lượt") },
                                placeholder = { Text("Trống = Không giới hạn") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Text("Đã có ${state.usedCount} khách hàng sử dụng Voucher này.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)

                        OutlinedTextField(
                            value = state.expiryDateStr,
                            onValueChange = { viewModel.onEvent(VoucherEditEvent.ExpiryDateChanged(it)) },
                            label = { Text("Ngày hết hạn") },
                            placeholder = { Text("VD: 2026-12-31 23:59") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            supportingText = { Text("Định dạng: Năm-Tháng-Ngày Giờ:Phút") }
                        )

                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

// Custom Chip Component cho đẹp
@Composable
fun SelectableChip(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}