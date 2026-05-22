package com.example.myapplication.presentation.screen.admin.cinema.create

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
fun AdminCinemaCreateScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminCinemaCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // 🔥 XỬ LÝ TOAST VÀ ĐIỀU HƯỚNG
    LaunchedEffect(state.isSuccess, state.error) {
        if (state.isSuccess) {
            Toast.makeText(context, "Thêm Rạp thành công!", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError() // Xóa lỗi sau khi hiện Toast
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onEvent(CinemaCreateEvent.LogoPicked(uri)) }
    )

    Scaffold(
        modifier = Modifier
            .imePadding()
            // Ẩn bàn phím khi bấm vào khoảng trống
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            TopAppBar(
                title = { Text("Thêm Rạp Chiếu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                windowInsets = WindowInsets(0.dp),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    focusManager.clearFocus() // Cất bàn phím trước khi lưu
                    viewModel.createCinema(context)
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Lưu Rạp", style = MaterialTheme.typography.titleMedium)
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
            // 1. Ảnh Logo rạp
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
                if (state.logoUri != null) {
                    AsyncImage(
                        model = state.logoUri,
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

            // 2. Thông tin cơ bản (Có validation và nhảy ô thông minh)
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(CinemaCreateEvent.NameChanged(it)) },
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
                onValueChange = { viewModel.onEvent(CinemaCreateEvent.AddressChanged(it)) },
                label = { Text("Địa chỉ chi tiết (*)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                isError = state.addressError != null,
                supportingText = { state.addressError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectOrTypeDropdown(
                    value = state.cineplex,
                    onValueChange = { viewModel.onEvent(CinemaCreateEvent.CineplexChanged(it)) },
                    label = "Cụm Rạp (Cineplex)",
                    suggestions = state.availableCineplexes,
                    modifier = Modifier.weight(1f)
                )

                SelectOrTypeDropdown(
                    value = state.region,
                    onValueChange = { viewModel.onEvent(CinemaCreateEvent.RegionChanged(it)) },
                    label = "Khu vực (Tỉnh/TP)",
                    suggestions = state.availableRegions,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = state.googleMapsLink,
                onValueChange = { viewModel.onEvent(CinemaCreateEvent.GoogleMapsLinkChanged(it)) },
                label = { Text("Dán link Google Maps (Tự động lấy toạ độ)") },
                placeholder = { Text("VD: https://www.google.com/maps/.../@21.02,105.80,15z") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )

            // 4. Toạ độ Bản đồ (Bắt lỗi nếu nhập text bậy bạ)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state.latitude,
                    onValueChange = { viewModel.onEvent(CinemaCreateEvent.LatitudeChanged(it)) },
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
                    onValueChange = { viewModel.onEvent(CinemaCreateEvent.LongitudeChanged(it)) },
                    label = { Text("Kinh độ (Lng)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = state.lngError != null,
                    supportingText = { state.lngError?.let { Text(it) } }
                )
            }

            // 5. Mô tả
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(CinemaCreateEvent.DescriptionChanged(it)) },
                label = { Text("Giới thiệu về Rạp") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            // 6. Trạng thái hoạt động
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.isActive,
                    onCheckedChange = { viewModel.onEvent(CinemaCreateEvent.IsActiveChanged(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (state.isActive) "Đang hoạt động (Khách thấy được rạp)" else "Tạm đóng cửa")
            }
        }
    }
}