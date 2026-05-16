package com.example.myapplication.presentation.screen.admin.booking.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.AdminBookingDetailDto
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingDetailScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    viewModel: AdminBookingDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết Đơn vé", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.error != null -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadDetail() }) { Text("Thử lại") }
                    }
                }
                state.detail != null -> {
                    BookingDetailContent(detail = state.detail!!)
                }
            }
        }
    }
}

@Composable
fun BookingDetailContent(detail: AdminBookingDetailDto) {
    val formatMoney = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // --- 1. THÔNG TIN CHUNG VÀ MÃ QR ---
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                // Mã QR Code (nếu có)
                if (!detail.qrCodeUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = detail.qrCodeUrl,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(150.dp).padding(bottom = 8.dp)
                    )
                }

                Text("MÃ VÉ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(detail.ticketCode, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(12.dp))

                // Trạng thái (Tô màu cho ngầu)
                val statusColor = when(detail.status) {
                    "PAID" -> Color(0xFF4CAF50) // Xanh lá
                    "PENDING" -> Color(0xFFFF9800) // Cam
                    "CANCELLED" -> MaterialTheme.colorScheme.error
                    else -> Color.Gray
                }
                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = detail.status,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Ngày tạo: ${detail.createdAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (detail.status == "CANCELLED" && detail.cancelledAt != null) {
                    Text("Đã hủy lúc: ${detail.cancelledAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        // --- 2. THÔNG TIN KHÁCH HÀNG ---
        SectionCard(title = "Thông tin Khách hàng", icon = Icons.Default.Person) {
            InfoRow(label = "Họ tên", value = detail.userName)
            InfoRow(label = "Email", value = detail.userEmail)
            if (!detail.userPhone.isNullOrEmpty()) {
                InfoRow(label = "Số điện thoại", value = detail.userPhone)
            }
        }

        // --- 3. THÔNG TIN SUẤT CHIẾU ---
        SectionCard(title = "Thông tin Suất chiếu", icon = Icons.Default.Movie) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = detail.moviePosterUrl ?: "https://via.placeholder.com/150",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(70.dp, 100.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(detail.movieName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${detail.cinemaName} - ${detail.roomName}", style = MaterialTheme.typography.bodyMedium)
                    Text("Giờ chiếu: ${detail.showtimeTime}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // --- 4. CHI TIẾT DỊCH VỤ (GHẾ & BẮP NƯỚC) ---
        SectionCard(title = "Ghế & Combo", icon = Icons.Default.EventSeat) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (detail.seats.isNotEmpty()) {
                    Row {
                        Text("Ghế chọn: ", fontWeight = FontWeight.SemiBold)
                        Text(detail.seats, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
                if (detail.combos.isNotEmpty()) {
                    Row {
                        Text("Combo: ", fontWeight = FontWeight.SemiBold)
                        Text(detail.combos)
                    }
                }
            }
        }

        // --- 5. CHI TIẾT THANH TOÁN (HÓA ĐƠN) ---
        SectionCard(title = "Hóa đơn Thanh toán", icon = Icons.Default.Receipt) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow(label = "Tiền ghế", value = formatMoney.format(detail.seatAmount))
                if (detail.comboAmount > 0) InfoRow(label = "Tiền bắp nước", value = formatMoney.format(detail.comboAmount))
                if (detail.voucherDiscount > 0) InfoRow(label = "Giảm giá Voucher", value = "- ${formatMoney.format(detail.voucherDiscount)}", valueColor = Color(0xFF4CAF50))
                if (detail.pointDiscount > 0) InfoRow(label = "Đổi điểm", value = "- ${formatMoney.format(detail.pointDiscount)}", valueColor = Color(0xFF4CAF50))

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TỔNG CỘNG", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                    Text(formatMoney.format(detail.totalAmount), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// ================= CÁC COMPONENT DÙNG CHUNG CỦA MÀN NÀY =================

@Composable
fun SectionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}