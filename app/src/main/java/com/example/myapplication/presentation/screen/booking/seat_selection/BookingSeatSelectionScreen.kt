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
    // 1. LOAD DATA & EFFECTS
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
    // 2. MAIN LAYOUT (Sử dụng Scaffold)
    // ==============================
    Scaffold(
        bottomBar = {
            // Bao bọc cả Countdown và BottomBar vào khu vực dưới cùng
            Column(
                modifier = Modifier.background(Color.White)
            ) {
                // COUNTDOWN
                if (state.expiresAt != null) {
                    CountdownTimer(
                        expiresAt = state.expiresAt, // Đảm bảo hàm này parse đúng Long
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
                            Log.d("BookingSeatSelectionScreen", "DCM DEL HIEU")}
                            state.seatHoldSessionId?.let { sessionId ->
                                Log.d("BookingSeatSelectionScreen", "onContinueClick: $sessionId")
                                onContinueClick(sessionId)
                            }
                        }
                    )
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
                .background(Color(0xFFF4F4F4))
                .padding(paddingValues) // Padding tự động để không bị lẹm vào BottomBar
        ) {
            if (state.isLoading) {
                // LOADING HIỂN THỊ ĐÈ LÊN BOX HOẶC Ở GIỮA
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // NỘI DUNG CHÍNH (Chỉ cuộn phần nội dung nếu màn hình nhỏ)
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
    // 4. DIALOGS
    // ==============================
    if (showExpireDialog) {
        AlertDialog(
            onDismissRequest = { /* Không cho dismiss bằng cách bấm ra ngoài */ },
            title = {
                Text("Hết thời gian giữ ghế")
            },
            text = {
                Text("Phiên giữ ghế của bạn đã hết hạn. Các ghế đã được giải phóng. Vui lòng chọn lại.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExpireDialog = false
                        onSessionExpired()
                    }
                ) {
                    Text("Đồng ý")
                }
            }
        )
    }
}