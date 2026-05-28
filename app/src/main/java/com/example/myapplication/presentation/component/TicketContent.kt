package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.BookingMyBookingDto
import com.example.myapplication.utils.formatPrice
import com.example.myapplication.utils.formatTicketTime
import kotlin.math.abs
@Composable
fun TicketContent(booking: BookingMyBookingDto) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Phần 1: Ảnh & Thông tin rạp ---
                AsyncImage(
                    model = booking.movie.posterUrl,
                    contentDescription = "Poster phim",
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = booking.movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${booking.cinema.name} - ${booking.room.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = booking.cinema.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // --- Phần 2: Thời gian & Ghế ngồi ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 🔥 Cột trái: Giờ chiếu (Chiếm 50% không gian)
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("Giờ chiếu", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = formatTicketTime(booking.showtimeStart),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // 🔥 Cột phải: Ghế ngồi (Chiếm 50% không gian và tự động ép Text xuống dòng)
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("Ghế ngồi", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                        val seatsString = booking.seats.joinToString(", ") { "${it.seatRow}${it.seatNumber}" }
                        Text(
                            text = seatsString,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.End // Căn lề phải cho chữ
                        )
                    }
                }

                // --- Phần 3: Combo ăn uống ---
                if (booking.combos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Combo ăn uống",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    booking.combos.forEach { combo ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "${combo.quantity}x ${combo.comboName}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f) // Ép tên combo dài tự xuống dòng
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${formatPrice(combo.price)}đ", // 🔥 Format tiền Combo
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // --- Phần 4: Mã QR Code ---
                if (!booking.qrCodeUrl.isNullOrEmpty()) {
                    Text(
                        text = "Đưa mã này cho nhân viên soát vé",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = booking.qrCodeUrl,
                        contentDescription = "Mã QR",
                        modifier = Modifier.size(160.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "Mã vé: ${booking.ticketCode}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // --- Phần 5: Hóa đơn thanh toán ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Thông tin thanh toán",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Đảm bảo ReceiptRow nhận kiểu Double để tính toán, sau đó dùng formatPrice() bên trong nó
                    ReceiptRow(label = "Tiền ghế", value = booking.seatAmount)

                    if (booking.comboAmount > 0) {
                        ReceiptRow(label = "Tiền Combo", value = booking.comboAmount)
                    }
                    if (booking.voucherDiscount > 0) {
                        ReceiptRow(label = "Giảm giá Voucher", value = -booking.voucherDiscount, isDiscount = true)
                    }
                    if (booking.pointDiscount > 0) {
                        ReceiptRow(label = "Giảm giá Điểm", value = -booking.pointDiscount, isDiscount = true)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng cộng", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "${formatPrice(booking.totalAmount)}đ", // 🔥 Format Tổng tiền
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}