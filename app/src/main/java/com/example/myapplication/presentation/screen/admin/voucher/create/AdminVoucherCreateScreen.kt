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
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import com.example.myapplication.presentation.screen.admin.showtime.create.DateTimePickerField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVoucherCreateScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminVoucherCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // 🔥 XỬ LÝ TOAST & ĐIỀU HƯỚNG
    LaunchedEffect(state.isSuccess, state.error) {
        if (state.isSuccess) {
            Toast.makeText(context, "Phát hành Voucher thành công!", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thêm Voucher mới",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Thêm nếu sau này bạn có nút Action bên phải
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onEvent(VoucherCreateEvent.SaveClicked)
                },
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
            // Đã thay khối báo lỗi bằng Toast mượt mà

            // --- 1. MÃ CODE ---
            OutlinedTextField(
                value = state.code,
                onValueChange = { viewModel.onEvent(VoucherCreateEvent.CodeChanged(it)) },
                label = { Text("Mã Voucher (CODE) (*)") },
                placeholder = { Text("VD: MOVIE2026") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.codeError != null,
                supportingText = { state.codeError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                singleLine = true
            )

            // --- 2. SWITCH KÍCH HOẠT ---
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
                        Text(if (state.isActive) "Khách hàng có thể sử dụng luôn" else "Tạm ẩn", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = state.isActive,
                        onCheckedChange = { viewModel.onEvent(VoucherCreateEvent.IsActiveChanged(it)) }
                    )
                }
            }

            // --- 3. LOẠI GIẢM GIÁ ---
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

            // --- 4. MỨC GIẢM GIÁ ---
            OutlinedTextField(
                value = state.discountValueStr,
                onValueChange = { viewModel.onEvent(VoucherCreateEvent.ValueChanged(it)) },
                label = { Text(if (state.discountType == "PERCENT") "Mức giảm (%) (*)" else "Mức giảm (VNĐ) (*)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.discountValueError != null,
                supportingText = { state.discountValueError?.let { Text(it) } }
            )

            // Giảm tối đa (Chỉ hiện khi là PERCENT)
            if (state.discountType == "PERCENT") {
                OutlinedTextField(
                    value = state.maxDiscountStr,
                    onValueChange = { viewModel.onEvent(VoucherCreateEvent.MaxDiscountChanged(it)) },
                    label = { Text("Giảm tối đa (VNĐ) - Tùy chọn") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Điều kiện áp dụng", fontWeight = FontWeight.SemiBold)

            // --- 5. ĐIỀU KIỆN & GIỚI HẠN ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state.minOrderValueStr,
                    onValueChange = { viewModel.onEvent(VoucherCreateEvent.MinOrderChanged(it)) },
                    label = { Text("Đơn tối thiểu (VNĐ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.usageLimitStr,
                    onValueChange = { viewModel.onEvent(VoucherCreateEvent.UsageLimitChanged(it)) },
                    label = { Text("Tổng số lượt") },
                    placeholder = { Text("Vô hạn") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // --- 6. LỊCH CHỌN HẠN DÙNG (Dùng DateTimePickerField) ---
            DateTimePickerField(
                value = state.expiryDateStr,
                label = "Ngày hết hạn (Tùy chọn)",
                onDateTimeSelected = { viewModel.onEvent(VoucherCreateEvent.ExpiryDateChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                isError = state.expiryDateError != null,
                errorText = state.expiryDateError
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}