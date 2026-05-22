package com.example.myapplication.presentation.screen.admin.news.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.presentation.screen.admin.voucher.edit.SelectableChip
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.example.myapplication.presentation.screen.admin.showtime.create.DateTimePickerField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNewsCreateScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminNewsCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showVoucherDropdown by remember { mutableStateOf(false) }

    // 🔥 XỬ LÝ TOAST VÀ ĐIỀU HƯỚNG
    LaunchedEffect(state.isSuccess, state.error) {
        if (state.isSuccess) {
            Toast.makeText(context, "Phát hành bài viết thành công!", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        viewModel.onEvent(NewsCreateEvent.ImageSelected(it))
    }

    Scaffold(
        modifier = Modifier
            .imePadding()
            // Chạm ra vùng trống để cất bàn phím
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = { Text("Tạo Bài viết Mới", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onEvent(NewsCreateEvent.SaveClicked(context))
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Phát hành tin tức", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- THUMBNAIL PICKER ---
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (state.selectedImageUri != null) {
                    AsyncImage(model = state.selectedImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Nhấn để tải ảnh bìa (*)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // --- FORM INPUTS CÓ VALIDATION ---
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(NewsCreateEvent.TitleChanged(it)) },
                label = { Text("Tiêu đề bài viết (*)") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.titleError != null,
                supportingText = { state.titleError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                singleLine = true
            )

            // --- LOẠI BÀI VIẾT ---
            Text("Phân loại bài viết", fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableChip(text = "Tin tức chung", isSelected = state.type == "NORMAL", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(NewsCreateEvent.TypeChanged("NORMAL")) })
                SelectableChip(text = "Kèm Voucher", isSelected = state.type == "VOUCHER", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(NewsCreateEvent.TypeChanged("VOUCHER")) })
            }

            // --- CHỌN VOUCHER ---
            if (state.type == "VOUCHER") {
                Box(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.availableVouchers.find { it.id == state.selectedVoucherId }?.code ?: "Nhấn để chọn Voucher đính kèm...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Voucher áp dụng (*)") },
                        modifier = Modifier.fillMaxWidth().clickable { showVoucherDropdown = true },
                        enabled = false, // Disable để bắt click mượt hơn
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary) }
                    )
                    DropdownMenu(expanded = showVoucherDropdown, onDismissRequest = { showVoucherDropdown = false }) {
                        state.availableVouchers.forEach { v ->
                            DropdownMenuItem(text = { Text(v.code) }, onClick = { viewModel.onEvent(NewsCreateEvent.VoucherSelected(v.id)); showVoucherDropdown = false })
                        }
                    }
                }
            }

            // --- NỘI DUNG ---
            OutlinedTextField(
                value = state.content,
                onValueChange = { viewModel.onEvent(NewsCreateEvent.ContentChanged(it)) },
                label = { Text("Nội dung bài viết") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            // --- LỊCH TRÌNH (DÙNG DATE TIME PICKER) ---
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Lịch trình hiển thị (Tùy chọn)", fontWeight = FontWeight.SemiBold)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DateTimePickerField(
                    value = state.startDateStr,
                    label = "Ngày bắt đầu",
                    onDateTimeSelected = { viewModel.onEvent(NewsCreateEvent.StartDateChanged(it)) },
                    modifier = Modifier.weight(1f),
                    isError = state.startDateError != null,
                    errorText = state.startDateError
                )
                DateTimePickerField(
                    value = state.endDateStr,
                    label = "Ngày kết thúc",
                    onDateTimeSelected = { viewModel.onEvent(NewsCreateEvent.EndDateChanged(it)) },
                    modifier = Modifier.weight(1f),
                    isError = state.endDateError != null,
                    errorText = state.endDateError
                )
            }

            // --- CÀI ĐẶT BỔ SUNG ---
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cài đặt bổ sung", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (state.published) "Xuất bản ngay (Hiển thị luôn)" else "Lưu nháp (Chưa hiển thị)")
                        Switch(checked = state.published, onCheckedChange = { viewModel.onEvent(NewsCreateEvent.PublishedChanged(it)) })
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Gửi thông báo (Push)", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = if (state.type == "VOUCHER") "Báo cho User biết có Voucher mới." else "Thông báo tin tức mới cho toàn bộ User.",
                                style = MaterialTheme.typography.bodySmall, color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = state.sendNotification, onCheckedChange = { viewModel.onEvent(NewsCreateEvent.SendNotificationChanged(it)) })
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}