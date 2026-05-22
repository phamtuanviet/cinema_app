package com.example.myapplication.presentation.screen.admin.combo.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminComboEditScreen(
    comboId: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminComboEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // 🔥 XỬ LÝ TOAST & ĐIỀU HƯỚNG
    LaunchedEffect(state.isSuccess, state.error) {
        if (state.isSuccess) {
            Toast.makeText(context, "Cập nhật Combo thành công!", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
        state.error?.let {
            if (!state.isLoadingData) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onEvent(ComboEditEvent.ImageSelected(uri)) }
    )

    Scaffold(
        modifier = Modifier
            .imePadding()
            // Chạm ra ngoài màn hình để cất bàn phím
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa Combo", fontWeight = FontWeight.Bold) },
                windowInsets = WindowInsets(0.dp),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        bottomBar = {
            if (!state.isLoadingData && state.error == null) {
                Button(
                    onClick = {
                        focusManager.clearFocus() // Cất bàn phím trước khi lưu
                        viewModel.onEvent(ComboEditEvent.SaveClicked(context))
                    },
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                // Đang tải dữ liệu
                state.isLoadingData -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                // Báo lỗi lúc fetch Data (ở giữa màn hình)
                state.error != null && state.name.isEmpty() -> {
                    Text(text = state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }

                // Hiển thị Form
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- CHỌN ẢNH ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val imageToDisplay = state.selectedImageUri ?: state.existingImageUrl

                            if (imageToDisplay != null) {
                                AsyncImage(
                                    model = imageToDisplay,
                                    contentDescription = "Ảnh Combo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.ImageSearch, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Nhấn để chọn ảnh mới", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        // --- THÔNG TIN COMBO (CÓ VALIDATION) ---
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.onEvent(ComboEditEvent.NameChanged(it)) },
                            label = { Text("Tên Combo (*)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = state.nameError != null,
                            supportingText = { state.nameError?.let { Text(it) } },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        OutlinedTextField(
                            value = state.priceStr,
                            onValueChange = { viewModel.onEvent(ComboEditEvent.PriceChanged(it)) },
                            label = { Text("Giá bán (VNĐ) (*)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = state.priceError != null,
                            supportingText = { state.priceError?.let { Text(it) } }
                        )

                        OutlinedTextField(
                            value = state.description,
                            onValueChange = { viewModel.onEvent(ComboEditEvent.DescriptionChanged(it)) },
                            label = { Text("Mô tả thành phần") },
                            placeholder = { Text("VD: 1 Bắp phô mai + 2 Nước ngọt") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            maxLines = 3,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )

                        // --- TRẠNG THÁI BÁN ---
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
                                    Text("Trạng thái", fontWeight = FontWeight.Bold)
                                    Text(if (state.isActive) "Đang bán" else "Ngừng bán", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = state.isActive,
                                    onCheckedChange = { viewModel.onEvent(ComboEditEvent.IsActiveChanged(it)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}