package com.example.myapplication.presentation.screen.admin.banner.edit

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.PopupProperties


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBannerEditScreen(
    bannerId: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminBannerEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showMovieDropdown by remember { mutableStateOf(false) }

    // 🔥 XỬ LÝ TOAST VÀ ĐIỀU HƯỚNG
    LaunchedEffect(state.isSuccess, state.error) {
        if (state.isSuccess) {
            Toast.makeText(context, "Cập nhật Banner thành công!", Toast.LENGTH_SHORT).show()
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
        viewModel.onEvent(BannerEditEvent.ImageSelected(it))
    }

    Scaffold(
        modifier = Modifier
            .imePadding()
            // Chạm ra ngoài để ẩn bàn phím
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chỉnh sửa Banner",
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
                        viewModel.onEvent(BannerEditEvent.SaveClicked(context))
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
                state.isLoadingData -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                // Hiển thị lỗi giữa màn hình nếu API gọi ban đầu thất bại (vd mất mạng)
                state.error != null && state.availableMovies.isEmpty() -> {
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
                        // --- 1. ẢNH BANNER ---
                        Box(
                            modifier = Modifier.fillMaxWidth().aspectRatio(21f / 9f).clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            contentAlignment = Alignment.Center
                        ) {
                            val img = state.selectedImageUri ?: state.existingImageUrl
                            if (img != null) {
                                AsyncImage(model = img, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddPhotoAlternate, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Nhấn để chọn ảnh mới", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        // --- 2. LOẠI HÀNH ĐỘNG ---
                        Text("Điều hướng khi người dùng nhấn vào Banner", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SelectableChip(text = "Chuyển đến Phim", isSelected = state.actionType == "MOVIE", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(BannerEditEvent.ActionTypeChanged("MOVIE")) })
                            SelectableChip(text = "Mở Web URL", isSelected = state.actionType == "URL", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(BannerEditEvent.ActionTypeChanged("URL")) })
                        }

                        // --- 3. DROPDOWN HOẶC Ô NHẬP LINK ---
                        if (state.actionType == "MOVIE") {
                            val filteredMovies = state.availableMovies.filter {
                                it.title.contains(state.movieSearchQuery, ignoreCase = true)
                            }

                            // 🔥 Chuyển sang dùng Box thường, bãi bỏ ExposedDropdownMenuBox
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = state.movieSearchQuery,
                                    onValueChange = {
                                        // Cập nhật text đang gõ
                                        viewModel.onEvent(BannerEditEvent.MovieSearchQueryChanged(it)) // Dùng BannerCreateEvent nếu ở màn Create
                                        showMovieDropdown = true
                                    },
                                    label = { Text("Phim liên kết (*)") },
                                    placeholder = { Text("Gõ tên phim để tìm...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = state.movieError != null,
                                    supportingText = { state.movieError?.let { Text(it) } },
                                    trailingIcon = {
                                        IconButton(onClick = { showMovieDropdown = !showMovieDropdown }) {
                                            Icon(
                                                imageVector = if (showMovieDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = "Toggle Dropdown"
                                            )
                                        }
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                                )

                                // 🔥 DropdownMenu thủ công
                                DropdownMenu(
                                    // Chỉ xổ xuống khi cờ show = true VÀ có dữ liệu khớp
                                    expanded = showMovieDropdown && filteredMovies.isNotEmpty(),
                                    onDismissRequest = { showMovieDropdown = false },

                                    properties = PopupProperties(focusable = false),

                                    modifier = Modifier
                                        .fillMaxWidth(0.9f) // Căn độ rộng tương đối cho Menu
                                        .heightIn(max = 250.dp) // Tránh menu dài lấn hết màn hình
                                ) {
                                    filteredMovies.forEach { movie ->
                                        DropdownMenuItem(
                                            text = { Text(movie.title) },
                                            onClick = {
                                                viewModel.onEvent(BannerEditEvent.MovieSelected(movie.id, movie.title)) // Dùng BannerCreateEvent nếu ở màn Create
                                                showMovieDropdown = false
                                                focusManager.clearFocus()
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = state.targetUrl,
                                onValueChange = { viewModel.onEvent(BannerEditEvent.TargetUrlChanged(it)) },
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
                            onValueChange = { viewModel.onEvent(BannerEditEvent.PriorityChanged(it)) },
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
                                    Text("Trạng thái", fontWeight = FontWeight.Bold)
                                    Text(if (state.isActive) "Đang hiển thị trên App" else "Đang ẩn", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(checked = state.isActive, onCheckedChange = { viewModel.onEvent(BannerEditEvent.IsActiveChanged(it)) })
                            }
                        }

                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}