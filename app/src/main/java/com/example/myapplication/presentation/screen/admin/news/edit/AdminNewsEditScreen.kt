package com.example.myapplication.presentation.screen.admin.news.edit

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
fun AdminNewsEditScreen(
    newsId: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminNewsEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showVoucherDropdown by remember { mutableStateOf(false) }

    // 🔥 XỬ LÝ TOAST & ĐIỀU HƯỚNG
    LaunchedEffect(state.isSuccess, state.error) {
        if (state.isSuccess) {
            Toast.makeText(context, "Cập nhật bài viết thành công!", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
        state.error?.let {
            if (!state.isLoadingData) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        viewModel.onEvent(NewsEditEvent.ImageSelected(it))
    }

    Scaffold(
        modifier = Modifier
            .imePadding()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chỉnh sửa bài viết",
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
            if (!state.isLoadingData && state.error == null) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onEvent(NewsEditEvent.SaveClicked(context))
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else Text("Cập nhật bài viết", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoadingData -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                // Lỗi khi load dữ liệu ban đầu
                state.error != null && state.title.isEmpty() -> {
                    Text(text = state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- THUMBNAIL ---
                        Box(
                            modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            contentAlignment = Alignment.Center
                        ) {
                            val img = state.selectedImageUri ?: state.existingThumbnailUrl
                            if (img != null) {
                                AsyncImage(model = img, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddPhotoAlternate, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Nhấn để tải ảnh bìa (*)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        // --- TIÊU ĐỀ ---
                        OutlinedTextField(
                            value = state.title,
                            onValueChange = { viewModel.onEvent(NewsEditEvent.TitleChanged(it)) },
                            label = { Text("Tiêu đề bài viết (*)") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.titleError != null,
                            supportingText = { state.titleError?.let { Text(it) } },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true
                        )

                        // --- PHÂN LOẠI ---
                        Text("Phân loại bài viết", fontWeight = FontWeight.SemiBold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SelectableChip(text = "Tin tức chung", isSelected = state.type == "NORMAL", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(NewsEditEvent.TypeChanged("NORMAL")) })
                            SelectableChip(text = "Kèm Voucher", isSelected = state.type == "VOUCHER", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(NewsEditEvent.TypeChanged("VOUCHER")) })
                        }

                        // --- CHỌN VOUCHER ---
                        if (state.type == "VOUCHER") {
                            Box(Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = state.availableVouchers.find { it.id == state.selectedVoucherId }?.code ?: "Chọn Voucher đính kèm...",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Voucher áp dụng (*)") },
                                    modifier = Modifier.fillMaxWidth().clickable { showVoucherDropdown = true },
                                    enabled = false,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary) }
                                )
                                DropdownMenu(expanded = showVoucherDropdown, onDismissRequest = { showVoucherDropdown = false }) {
                                    state.availableVouchers.forEach { v ->
                                        DropdownMenuItem(text = { Text(v.code) }, onClick = { viewModel.onEvent(NewsEditEvent.VoucherSelected(v.id)); showVoucherDropdown = false })
                                    }
                                }
                            }
                        }

                        // --- NỘI DUNG VĂN BẢN ---
                        OutlinedTextField(
                            value = state.content,
                            onValueChange = { viewModel.onEvent(NewsEditEvent.ContentChanged(it)) },
                            label = { Text("Nội dung bài viết") },
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            placeholder = { Text("Nhập nội dung chi tiết bài viết tại đây...") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )

                        // --- LỊCH TRÌNH ---
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Lịch trình hiển thị (Tùy chọn)", fontWeight = FontWeight.SemiBold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DateTimePickerField(
                                value = state.startDateStr,
                                label = "Ngày bắt đầu",
                                onDateTimeSelected = { viewModel.onEvent(NewsEditEvent.StartDateChanged(it)) },
                                modifier = Modifier.weight(1f),
                                isError = state.startDateError != null,
                                errorText = state.startDateError
                            )
                            DateTimePickerField(
                                value = state.endDateStr,
                                label = "Ngày kết thúc",
                                onDateTimeSelected = { viewModel.onEvent(NewsEditEvent.EndDateChanged(it)) },
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
                                    Text(if (state.published) "Xuất bản (Hiển thị ngay)" else "Bản nháp (Đang ẩn)")
                                    Switch(checked = state.published, onCheckedChange = { viewModel.onEvent(NewsEditEvent.PublishedChanged(it)) })
                                }
                            }
                        }

                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}