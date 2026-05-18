package com.example.myapplication.presentation.screen.admin.voucher.create


import androidx.compose.foundation.background
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.admin.voucher.edit.SelectableChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVoucherCreateScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminVoucherCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSaveSuccess()
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = { Text("Thêm Voucher Mới", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.onEvent(VoucherCreateEvent.SaveClicked) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text("Phát hành Voucher", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.error != null) {
                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp)) {
                    Text(text = state.error!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp))
                }
            }

            // Mã Code
            OutlinedTextField(
                value = state.code,
                onValueChange = { viewModel.onEvent(VoucherCreateEvent.CodeChanged(it)) },
                label = { Text("Mã Voucher (CODE) (*)") },
                placeholder = { Text("VD: MOVIE2026") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                singleLine = true
            )

            // Switch kích hoạt luôn hay không
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
                        Text("Kích hoạt ngay", fontWeight = FontWeight.Bold)
                        Text(if (state.isActive) "Cho phép khách hàng sử dụng luôn" else "Tạm ẩn", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = state.isActive,
                        onCheckedChange = { viewModel.onEvent(VoucherCreateEvent.IsActiveChanged(it)) }
                    )
                }
            }

            // Loại giảm giá
            Text("Loại giảm giá", fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableChip(
                    text = "Phần trăm (%)",
                    isSelected = state.discountType == "PERCENT",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onEvent(VoucherCreateEvent.TypeChanged("PERCENT")) }
                )
                SelectableChip(
                    text = "Tiền mặt (VNĐ)",
                    isSelected = state.discountType == "FIXED",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onEvent(VoucherCreateEvent.TypeChanged("FIXED")) }
                )
            }

            // Mức giảm giá
            OutlinedTextField(
                value = state.discountValueStr,
                onValueChange = { viewModel.onEvent(VoucherCreateEvent.ValueChanged(it)) },
                label = { Text(if (state.discountType == "PERCENT") "Mức giảm (%) (*)" else "Mức giảm (VNĐ) (*)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Giảm tối đa (Chỉ hiện khi là PERCENT)
            if (state.discountType == "PERCENT") {
                OutlinedTextField(
                    value = state.maxDiscountStr,
                    onValueChange = { viewModel.onEvent(VoucherCreateEvent.MaxDiscountChanged(it)) },
                    label = { Text("Giảm tối đa (VNĐ) - Tùy chọn") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Điều kiện áp dụng", fontWeight = FontWeight.SemiBold)

            // Đơn tối thiểu
            OutlinedTextField(
                value = state.minOrderValueStr,
                onValueChange = { viewModel.onEvent(VoucherCreateEvent.MinOrderChanged(it)) },
                label = { Text("Giá trị đơn hàng tối thiểu (VNĐ) - Tùy chọn") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Giới hạn lượt dùng
            OutlinedTextField(
                value = state.usageLimitStr,
                onValueChange = { viewModel.onEvent(VoucherCreateEvent.UsageLimitChanged(it)) },
                label = { Text("Tổng số lượt phát hành") },
                placeholder = { Text("Để trống nếu không giới hạn") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Hạn dùng
            OutlinedTextField(
                value = state.expiryDateStr,
                onValueChange = { viewModel.onEvent(VoucherCreateEvent.ExpiryDateChanged(it)) },
                label = { Text("Ngày hết hạn") },
                placeholder = { Text("VD: 2026-12-31 23:59") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("Định dạng: Năm-Tháng-Ngày Giờ:Phút") }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}