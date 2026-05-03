package com.example.myapplication.presentation.screen.profile.ticket_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.TicketContent
import com.example.myapplication.utils.canRefundTicket



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTicketDetailScreen(
    viewModel: ProfileTicketDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết vé") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                windowInsets = WindowInsets(0.dp)
            )
        },
        bottomBar = {
            state.booking?.let { booking ->
                val isRefundable = canRefundTicket(
                    showtimeStartStr = booking.showtimeStart,
                    status = booking.status
                )

                if (isRefundable && !state.refundSuccess) {
                    Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Hủy và Hoàn vé (Refund)")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Hiển thị Loading lúc mới mở vé
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Hiển thị Lỗi
            if (!state.isLoading && state.error != null && !state.isRefunding) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Hiển thị Chi tiết vé nếu có data
            if (!state.isLoading && state.booking != null) {
                TicketContent(booking = state.booking!!)
            }

            // ==============================================================
            // 1. DIALOG XÁC NHẬN HỦY VÉ (Hỏi lại cho chắc)
            // ==============================================================
            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("Xác nhận hoàn vé") },
                    text = { Text("Bạn có chắc chắn muốn hủy và hoàn tiền vé này không? Hành động này không thể hoàn tác.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showConfirmDialog = false
                                viewModel.requestRefund() // Gọi API hoàn tiền
                            }
                        ) {
                            Text("Đồng ý hủy", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("Bỏ qua")
                        }
                    }
                )
            }

            // ==============================================================
            // 2. MÀN HÌNH CHỜ XỬ LÝ HOÀN TIỀN (Tránh user hoang mang)
            // ==============================================================
            if (state.isRefunding) {
                Dialog(
                    onDismissRequest = { /* Chặn không cho ấn ra ngoài tắt */ },
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Đang kết nối với VNPAY...",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Quá trình này có thể mất chút thời gian. Vui lòng không tắt ứng dụng lúc này.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ==============================================================
            // 3. THÔNG BÁO HOÀN TIỀN THÀNH CÔNG VÀ NÚT VỀ TRANG CHỦ
            // ==============================================================
            if (state.refundSuccess) {
                AlertDialog(
                    onDismissRequest = { /* Ép user phải bấm nút Về trang chủ */ },
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    ),
                    title = { Text("Hoàn vé thành công!") },
                    text = {
                        Text("Số tiền của bạn có thể được hoàn trả về tài khoản trong vòng 3 ngày làm việc.\n\nCác voucher và điểm thưởng (nếu có) đã được tự động trả lại.")
                    },
                    confirmButton = {
                        Button(
                            onClick = { onNavigateHome() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Về màn hình chính")
                        }
                    }
                )
            }
        }
    }
}
