package com.example.myapplication.presentation.screen.admin.showtime.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.remote.dto.SimpleItemDto

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.material.icons.filled.CalendarToday
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminShowtimeCreateScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminShowtimeCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    // Tự động thoát khi lưu thành công
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
                title = { Text("Tạo Lịch chiếu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.createShowtime() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Lưu Lịch chiếu", style = MaterialTheme.typography.titleMedium)
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
            // Hiển thị lỗi tổng (nếu có)
            if (state.error != null) {
                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small) {
                    Text(text = state.error!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp))
                }
            }

            Text("1. Chọn Phim & Rạp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            // --- TÌM KIẾM PHIM ---
            AutoCompleteSearchDropdown(
                query = state.movieSearchQuery,
                onQueryChange = { viewModel.onEvent(ShowtimeCreateEvent.MovieQueryChanged(it)) },
                label = "Tên Phim (*)",
                suggestions = state.movieSuggestions,
                onItemSelected = { viewModel.onEvent(ShowtimeCreateEvent.MovieSelected(it)) }
            )

            // --- TÌM KIẾM RẠP ---
            AutoCompleteSearchDropdown(
                query = state.cinemaSearchQuery,
                onQueryChange = { viewModel.onEvent(ShowtimeCreateEvent.CinemaQueryChanged(it)) },
                label = "Tên Rạp (*)",
                suggestions = state.cinemaSuggestions,
                onItemSelected = { viewModel.onEvent(ShowtimeCreateEvent.CinemaSelected(it)) }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("2. Thời gian chiếu", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            // --- THỜI GIAN (ĐÃ DÙNG PICKER) ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DateTimePickerField(
                    value = state.startTimeStr,
                    label = "Bắt đầu (*)",
                    onDateTimeSelected = { viewModel.onEvent(ShowtimeCreateEvent.StartTimeChanged(it)) },
                    modifier = Modifier.weight(1f)
                )

                DateTimePickerField(
                    value = state.endTimeStr,
                    label = "Kết thúc (*)",
                    onDateTimeSelected = { viewModel.onEvent(ShowtimeCreateEvent.EndTimeChanged(it)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("3. Chọn Phòng (Tự động tải phòng trống)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            // --- CHỌN PHÒNG (Chỉ hiện phòng trống) ---
            ReadOnlySelectionDropdown(
                selectedValue = state.selectedRoom?.name ?: "Vui lòng chọn Rạp và nhập giờ trước",
                label = "Phòng chiếu (*)",
                options = state.availableRooms,
                onOptionSelected = { viewModel.onEvent(ShowtimeCreateEvent.RoomSelected(it)) },
                isLoading = state.isLoadingRooms,
                enabled = state.availableRooms.isNotEmpty()
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("4. Thiết lập Giá vé", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            // --- GIÁ VÉ (Đã đổi label weekendModifier) ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state.basePrice,
                    onValueChange = { viewModel.onEvent(ShowtimeCreateEvent.BasePriceChanged(it)) },
                    label = { Text("Giá cơ bản (VNĐ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.weekendModifier,
                    onValueChange = { viewModel.onEvent(ShowtimeCreateEvent.WeekendModifierChanged(it)) },
                    label = { Text("Phụ thu cuối tuần (+ VNĐ)") }, // 🔥 Đổi nhãn ở đây
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ================= CÁC COMPONENT TÙY CHỈNH =================

// Component 1: Gõ tới đâu, gọi API gợi ý tới đó (Dùng cho Phim, Rạp)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteSearchDropdown(
    query: String,
    onQueryChange: (String) -> Unit,
    label: String,
    suggestions: List<SimpleItemDto>,
    onItemSelected: (SimpleItemDto) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && suggestions.isNotEmpty(),
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = true
            },
            label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        ExposedDropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            suggestions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Component 2: Chỉ bấm để chọn từ danh sách đã load (Dùng cho Phòng chiếu)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadOnlySelectionDropdown(
    selectedValue: String,
    label: String,
    options: List<SimpleItemDto>,
    onOptionSelected: (SimpleItemDto) -> Unit,
    isLoading: Boolean,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        onOptionSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DateTimePickerField(
    value: String,
    label: String,
    onDateTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Dialog chọn Giờ (Mở sau khi chọn Ngày xong)
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            // Format chuẩn lại thành chuỗi yyyy-MM-dd HH:mm
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            onDateTimeSelected(formatter.format(calendar.time))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // Sử dụng định dạng 24h
    )

    // Dialog chọn Ngày
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            // Lập tức hiển thị bảng chọn Giờ
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Dùng Box đè lên để bắt sự kiện Click toàn bộ ô TextField
    Box(modifier = modifier.clickable { datePickerDialog.show() }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true, // Khóa không cho gõ tay
            label = { Text(label) },
            placeholder = { Text("Chọn ngày & giờ") },
            trailingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = "Chọn thời gian", tint = MaterialTheme.colorScheme.primary)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = false, // Vô hiệu hóa để chặn bàn phím ảo bật lên
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}