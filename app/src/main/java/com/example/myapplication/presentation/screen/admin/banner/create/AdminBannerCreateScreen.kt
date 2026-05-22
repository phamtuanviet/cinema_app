package com.example.myapplication.presentation.screen.admin.banner.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import com.example.myapplication.presentation.screen.admin.voucher.edit.SelectableChip
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBannerCreateScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminBannerCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showMovieDropdown by remember { mutableStateOf(false) }

    // 🔥 XỬ LÝ TOAST VÀ ĐIỀU HƯỚNG
    LaunchedEffect(state.isSuccess, state.error) {
        if (state.isSuccess) {
            Toast.makeText(context, "Thêm Banner thành công!", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        viewModel.onEvent(BannerCreateEvent.ImageSelected(it))
    }

    Scaffold(
        modifier = Modifier
            .imePadding()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = { Text("Thêm Banner Mới", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onEvent(BannerCreateEvent.SaveClicked(context))
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Đăng Banner", style = MaterialTheme.typography.titleMedium)
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
            // Đã xóa khối hiển thị lỗi (Surface) cồng kềnh

            // --- 1. ẢNH BANNER ---
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(21f / 9f).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (state.selectedImageUri != null) {
                    AsyncImage(model = state.selectedImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nhấn để chọn ảnh bìa (Bắt buộc)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // --- 2. LOẠI HÀNH ĐỘNG ---
            Text("Điều hướng khi người dùng nhấn vào Banner", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableChip(text = "Chuyển đến Phim", isSelected = state.actionType == "MOVIE", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(BannerCreateEvent.ActionTypeChanged("MOVIE")) })
                SelectableChip(text = "Mở Web URL", isSelected = state.actionType == "URL", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(BannerCreateEvent.ActionTypeChanged("URL")) })
            }

            // --- 3. HIỂN THỊ DROPDOWN HOẶC Ô NHẬP LINK (CÓ VALIDATION) ---
            if (state.actionType == "MOVIE") {
                Box(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.availableMovies.find { it.id == state.selectedMovieId }?.title ?: "Nhấn để chọn Phim...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Phim liên kết (*)") },
                        modifier = Modifier.fillMaxWidth().clickable { showMovieDropdown = true },
                        enabled = false, // Vô hiệu hóa gõ để nhận tương tác Clickable tốt hơn
                        isError = state.movieError != null,
                        supportingText = { state.movieError?.let { Text(it) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = if (state.movieError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            disabledLabelColor = if (state.movieError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary) }
                    )
                    DropdownMenu(expanded = showMovieDropdown, onDismissRequest = { showMovieDropdown = false }) {
                        state.availableMovies.forEach { m ->
                            DropdownMenuItem(text = { Text(m.title) }, onClick = { viewModel.onEvent(BannerCreateEvent.MovieSelected(m.id)); showMovieDropdown = false })
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = state.targetUrl,
                    onValueChange = { viewModel.onEvent(BannerCreateEvent.TargetUrlChanged(it)) },
                    label = { Text("Đường dẫn Web (URL) (*)") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.targetUrlError != null,
                    supportingText = { state.targetUrlError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    singleLine = true
                )
            }

            // --- 4. MỨC ĐỘ ƯU TIÊN & TRẠNG THÁI ---
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            OutlinedTextField(
                value = state.priorityStr,
                onValueChange = { viewModel.onEvent(BannerCreateEvent.PriorityChanged(it)) },
                label = { Text("Mức độ ưu tiên (0 = Thấp nhất)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Kích hoạt ngay", fontWeight = FontWeight.Bold)
                        Text(if (state.isActive) "Hiển thị trên App" else "Lưu dưới dạng bản nháp", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = state.isActive, onCheckedChange = { viewModel.onEvent(BannerCreateEvent.IsActiveChanged(it)) })
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}