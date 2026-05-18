package com.example.myapplication.presentation.screen.booking.seat_selection

import SeatGrid
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.BottomBarSelection
import com.example.myapplication.presentation.component.CountdownTimer
import com.example.myapplication.presentation.component.MovieHeader
import com.example.myapplication.presentation.component.ScreenIndicator
import com.example.myapplication.presentation.component.SeatLegend
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BookingSeatSelectionScreen(
    showtimeId: String,
    onContinueClick: (sessionId: String) -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: BookingSeatSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showExpireDialog by remember { mutableStateOf(false) }

    // ==============================
    // 1. LOAD DATA & EFFECTS (Giữ nguyên Logic)
    // ==============================
    LaunchedEffect(showtimeId) {
        viewModel.loadData(showtimeId)
    }

    state.error?.let { error ->
        LaunchedEffect(error) {
            println("ERROR: $error")
            viewModel.clearError()
        }
    }

    // ==============================
    // 2. MAIN LAYOUT (Sử dụng Scaffold MD3)
    // ==============================
    Scaffold(
        // Scaffold MD3 mặc định sử dụng MaterialTheme.colorScheme.background làm màu nền,
        // tự động tương thích Sáng/Tối.
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Bao bọc bằng Surface của MD3 để tạo độ nổi (elevation) và màu sắc nền tảng
            // tự động thích ứng với Dark Mode thay vì dùng Color.White
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer, // Màu nền khuyên dùng cho BottomBar MD3
                tonalElevation = 8.dp, // Tạo hiệu ứng nổi nhẹ
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 16.dp) // Thêm padding cho thanh điều hướng dưới của điện thoại
                ) {
                    // COUNTDOWN
                    if (state.expiresAt != null) {
                        CountdownTimer(
                            expiresAt = state.expiresAt,
                            onExpire = { showExpireDialog = true }
                        )
                    }

                    // BOTTOM BAR TIẾP TỤC
                    if (state.selectedSeats.isNotEmpty()) {
                        BottomBarSelection(
                            totalPrice = state.totalPrice,
                            selectedSeats = state.selectedSeatNames,
                            onContinueClick = { _ ->
                                val sessionId = state.seatHoldSessionId
                                if (sessionId == null) {
                                    Log.d("BookingSeatSelectionScreen", "SessionId is null")
                                }
                                state.seatHoldSessionId?.let { validSessionId ->
                                    Log.d("BookingSeatSelectionScreen", "onContinueClick: $validSessionId")
                                    onContinueClick(validSessionId)
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        // ==============================
        // 3. CONTENT & LOADING STATE
        // ==============================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding tự động để không lẹm BottomBar
            // Đã xóa .background(Color(0xFFF4F4F4)) vì Scaffold đã lo việc này
        ) {
            if (state.isLoading) {
                // LOADING
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                // NỘI DUNG CHÍNH
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    state.movie?.let { movie ->
                        MovieHeader(movie)
                    }
                    SeatLegend()

                    ScreenIndicator()

                    SeatGrid(
                        rows = state.seatMap?.rows ?: emptyList(),
                        selectedSeats = state.selectedSeats,
                        onSeatClick = { seatIds ->
                            viewModel.toggleSeats(showtimeId, seatIds)
                        }
                    )
                }
            }
        }
    }

    // ==============================
    // 4. DIALOGS (Chuẩn AlertDialog MD3)
    // ==============================
    if (showExpireDialog) {
        AlertDialog(
            onDismissRequest = { /* Không cho dismiss */ },
            title = {
                Text(
                    text = "Hết thời gian giữ ghế",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "Phiên giữ ghế của bạn đã hết hạn. Các ghế đã được giải phóng. Vui lòng chọn lại.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExpireDialog = false
                        onSessionExpired()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Đồng ý")
                }
            },
            // Thuộc tính của MD3 giúp Dialog hiển thị đẹp hơn trong Dark/Light mode
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}