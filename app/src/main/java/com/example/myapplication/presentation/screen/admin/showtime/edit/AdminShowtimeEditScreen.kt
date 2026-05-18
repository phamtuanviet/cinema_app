package com.example.myapplication.presentation.screen.admin.showtime.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.admin.showtime.create.DateTimePickerField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminShowtimeEditScreen(
    showtimeId: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminShowtimeEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Thoát màn hình khi lưu hoặc hủy thành công
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSaveSuccess()
    }

    // --- DIALOG CẢNH BÁO HỦY LỊCH CHIẾU ---
    if (state.showCancelDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ShowtimeEditEvent.ToggleCancelDialog) },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Cảnh báo", tint = MaterialTheme.colorScheme.error) },
            title = { Text("Xác nhận Hủy Lịch chiếu?") },
            text = {
                Text("Hành động này sẽ đổi trạng thái lịch chiếu thành CANCELED. Vé đã bán (nếu có) có thể bị ảnh hưởng. Bạn có chắc chắn muốn tiếp tục?")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(ShowtimeEditEvent.ConfirmCancelShowtime) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Đồng ý Hủy", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.onEvent(ShowtimeEditEvent.ToggleCancelDialog) }) {
                    Text("Giữ lại")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = { Text("Chỉnh sửa Lịch chiếu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        bottomBar = {
            if (!state.isLoadingData && state.currentStatus != "CANCELED") {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Nút Lưu thay đổi giờ
                    Button(
                        onClick = { viewModel.onEvent(ShowtimeEditEvent.SaveTimeChanges) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !state.isSaving
                    ) {
                        if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        else Text("Cập nhật Giờ chiếu", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nút Hủy suất chiếu (Đỏ chót)
                    OutlinedButton(
                        onClick = { viewModel.onEvent(ShowtimeEditEvent.ToggleCancelDialog) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text("Hủy Lịch chiếu này", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
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
                state.isLoadingData -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null && state.movieName.isEmpty() -> {
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
                        // Báo lỗi form nếu cập nhật bị trùng giờ hoặc lỗi mạng
                        if (state.error != null) {
                            Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp)) {
                                Text(text = state.error!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp))
                            }
                        }

                        // Nếu Lịch chiếu đã bị hủy, hiện thông báo to đùng
                        if (state.currentStatus == "CANCELED") {
                            Surface(
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "LỊCH CHIẾU NÀY ĐÃ BỊ HỦY",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Text("1. Thông tin Lịch chiếu (Không thể sửa)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        // Card hiển thị thông tin Read-only
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row {
                                    Text("Phim: ", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(state.movieName, fontWeight = FontWeight.Bold)
                                }
                                Row {
                                    Text("Rạp & Phòng: ", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(state.cinemaRoomInfo, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("2. Chỉnh sửa Thời gian", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        // --- THỜI GIAN (Dùng Picker giống bên Create) ---
                        // Vô hiệu hóa Picker nếu lịch đã bị hủy
                        val isEditable = state.currentStatus != "CANCELED" && !state.isSaving

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            DateTimePickerField(
                                value = state.startTimeStr,
                                label = "Bắt đầu (*)",
                                onDateTimeSelected = { if (isEditable) viewModel.onEvent(ShowtimeEditEvent.StartTimeChanged(it)) },
                                modifier = Modifier.weight(1f)
                            )

                            DateTimePickerField(
                                value = state.endTimeStr,
                                label = "Kết thúc (*)",
                                onDateTimeSelected = { if (isEditable) viewModel.onEvent(ShowtimeEditEvent.EndTimeChanged(it)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}