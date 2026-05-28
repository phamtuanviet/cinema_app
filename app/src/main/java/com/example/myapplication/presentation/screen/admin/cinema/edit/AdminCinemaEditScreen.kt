package com.example.myapplication.presentation.screen.admin.cinema.edit

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.presentation.component.SelectOrTypeDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCinemaEditScreen(
    cinemaId: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminCinemaEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // XỬ LÝ TOAST VÀ ĐIỀU HƯỚNG
    LaunchedEffect(state.isSuccess, state.error) {
        if (state.isSuccess) {
            Toast.makeText(context, "Cập nhật Rạp thành công!", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
        state.error?.let { errorMsg ->
            // Chỉ hiện Toast nếu KHÔNG PHẢI đang load dữ liệu (tránh hiện Toast lúc mới mở màn hình nếu có lỗi mạng)
            if (!state.isLoadingData) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onEvent(CinemaEditEvent.LogoPicked(uri)) }
    )

    Scaffold(
        modifier = Modifier
            .imePadding()
            // Chạm ra ngoài để cất bàn phím
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chỉnh sửa rạp",
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
                        viewModel.updateCinema(context)
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
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

                // 2. Lỗi khi lấy data (Đây là lỗi chặn đứng Form)
                state.error != null && state.name.isEmpty() -> {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // 3. Hiển thị Form chỉnh sửa (Tương tự Form Create)
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
                            singleLine = true,
                            isError = state.nameError != null,
                            supportingText = { state.nameError?.let { Text(it) } },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        OutlinedTextField(
                            value = state.address,
                            onValueChange = { viewModel.onEvent(CinemaEditEvent.AddressChanged(it)) },
                            label = { Text("Địa chỉ chi tiết (*)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            isError = state.addressError != null,
                            supportingText = { state.addressError?.let { Text(it) } },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        // --- DROPDOWN GỢI Ý ---
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
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
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
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                isError = state.latError != null,
                                supportingText = { state.latError?.let { Text(it) } }
                            )
                            OutlinedTextField(
                                value = state.longitude,
                                onValueChange = { viewModel.onEvent(CinemaEditEvent.LongitudeChanged(it)) },
                                label = { Text("Kinh độ (Lng)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                isError = state.lngError != null,
                                supportingText = { state.lngError?.let { Text(it) } }
                            )
                        }

                        // --- MÔ TẢ & TRẠNG THÁI ---
                        OutlinedTextField(
                            value = state.description,
                            onValueChange = { viewModel.onEvent(CinemaEditEvent.DescriptionChanged(it)) },
                            label = { Text("Giới thiệu về Rạp") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            maxLines = 4,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = state.isActive,
                                onCheckedChange = { viewModel.onEvent(CinemaEditEvent.IsActiveChanged(it)) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (state.isActive) "Đang hoạt động" else "Tạm đóng cửa")
                        }
                    }
                }
            }
        }
    }
}