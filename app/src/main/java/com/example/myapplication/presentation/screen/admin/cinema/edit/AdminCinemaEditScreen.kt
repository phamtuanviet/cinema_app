package com.example.myapplication.presentation.screen.admin.cinema.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.presentation.component.SelectOrTypeDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCinemaEditScreen(
    cinemaId: String, // Lấy từ tham số Navigation
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminCinemaEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Theo dõi trạng thái lưu thành công để thoát màn hình
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSaveSuccess()
    }

    // Launcher mở thư viện ảnh
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onEvent(CinemaEditEvent.LogoPicked(uri)) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa Rạp", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            // Chỉ hiện nút Lưu khi đã load xong dữ liệu
            if (!state.isLoadingData) {
                Button(
                    onClick = { viewModel.updateCinema(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else Text("Cập nhật Rạp", style = MaterialTheme.typography.titleMedium)
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
                // 1. Đang tải dữ liệu từ API
                state.isLoadingData -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // 2. Lỗi khi tải dữ liệu
                state.error != null && state.name.isEmpty() -> {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // 3. Hiển thị Form chỉnh sửa
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- ẢNH LOGO ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray)
                                .clickable {
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Ưu tiên hiện ảnh mới chọn, nếu không thì hiện ảnh cũ từ DB
                            val imageToShow = state.newLogoUri ?: state.currentLogoUrl

                            if (imageToShow != null) {
                                AsyncImage(
                                    model = imageToShow,
                                    contentDescription = "Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                                    Text("Nhấn để chọn Logo Rạp", color = Color.Gray)
                                }
                            }
                        }

                        // --- THÔNG TIN CƠ BẢN ---
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.onEvent(CinemaEditEvent.NameChanged(it)) },
                            label = { Text("Tên Rạp (*)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = state.address,
                            onValueChange = { viewModel.onEvent(CinemaEditEvent.AddressChanged(it)) },
                            label = { Text("Địa chỉ chi tiết (*)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )

                        // --- DROPDOWN GỢI Ý (Dùng chung Component SelectOrTypeDropdown) ---
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            SelectOrTypeDropdown(
                                value = state.cineplex,
                                onValueChange = { viewModel.onEvent(CinemaEditEvent.CineplexChanged(it)) },
                                label = "Cụm Rạp",
                                suggestions = state.availableCineplexes,
                                modifier = Modifier.weight(1f)
                            )

                            SelectOrTypeDropdown(
                                value = state.region,
                                onValueChange = { viewModel.onEvent(CinemaEditEvent.RegionChanged(it)) },
                                label = "Khu vực",
                                suggestions = state.availableRegions,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // --- TOẠ ĐỘ & LINK GOOGLE MAPS ---
                        OutlinedTextField(
                            value = state.googleMapsLink,
                            onValueChange = { viewModel.onEvent(CinemaEditEvent.GoogleMapsLinkChanged(it)) },
                            label = { Text("Dán link Google Maps (Tự động lấy toạ độ)") },
                            placeholder = { Text("VD: http://googleusercontent.com/maps.google.com/...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = state.latitude,
                                onValueChange = { viewModel.onEvent(CinemaEditEvent.LatitudeChanged(it)) },
                                label = { Text("Vĩ độ (Lat)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = state.longitude,
                                onValueChange = { viewModel.onEvent(CinemaEditEvent.LongitudeChanged(it)) },
                                label = { Text("Kinh độ (Lng)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // --- MÔ TẢ & TRẠNG THÁI ---
                        OutlinedTextField(
                            value = state.description,
                            onValueChange = { viewModel.onEvent(CinemaEditEvent.DescriptionChanged(it)) },
                            label = { Text("Giới thiệu về Rạp") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            maxLines = 4
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = state.isActive,
                                onCheckedChange = { viewModel.onEvent(CinemaEditEvent.IsActiveChanged(it)) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (state.isActive) "Đang hoạt động" else "Tạm đóng cửa")
                        }

                        // Hiển thị lỗi form (nếu có)
                        if (state.error != null) {
                            Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}