package com.example.myapplication.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text

import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.myapplication.presentation.screen.profile.loyalty.LoyaltyState
import com.example.myapplication.presentation.screen.voucher.voucher_list.VoucherListState
import com.example.myapplication.utils.formatShowtime
import com.example.myapplication.utils.formatTransactionDate
import com.example.myapplication.utils.localizeTransactionDescription


@Composable
fun LoyaltyTab(state: LoyaltyState) {
    Column(modifier = Modifier.fillMaxSize()) {

        // --- BẢNG TỔNG ĐIỂM NỔI BẬT ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            // Dùng gradient hoặc màu primary tươi sáng để làm nổi bật thẻ
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Stars,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, // Đổi màu icon cho rực rỡ
                    modifier = Modifier.size(56.dp) // Tăng size icon lên chút
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Điểm tích luỹ hiện tại",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "%,d".format(state.loyaltyPoint), // 🔥 Format có dấu phẩy ngăn cách hàng nghìn (VD: 1,500)
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = "Lịch sử điểm thưởng", // Việt hóa lại cho hợp ngữ cảnh
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // --- DANH SÁCH LỊCH SỬ ---
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Bạn chưa có giao dịch điểm thưởng nào.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                itemsIndexed(state.transactions) { index, transaction ->
                    val isPositive = transaction.points > 0
                    // Dùng màu xanh lá sẫm hơn một chút để chống lóa trên nền sáng, màu đỏ tươi cho điểm trừ
                    val pointColor = if (isPositive) Color(0xFF1B5E20) else MaterialTheme.colorScheme.error
                    val sign = if (isPositive) "+" else ""

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Tương lai có thể show popup chi tiết */ }
                            .padding(vertical = 14.dp, horizontal = 4.dp), // Tăng padding dọc lên một chút cho thoáng
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon trạng thái
                        Surface(
                            shape = CircleShape,
                            color = pointColor.copy(alpha = 0.08f), // 🔥 Giảm alpha xuống 0.08 để nền trong trẻo, sang trọng hơn
                            modifier = Modifier.size(48.dp) // 🔥 Tăng lên 48dp (chuẩn kích thước ngón tay của Material 3)
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.Rounded.AddCircleOutline else Icons.Rounded.RemoveCircleOutline,
                                contentDescription = null,
                                tint = pointColor,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Chi tiết giao dịch
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = localizeTransactionDescription(transaction.description),
                                style = MaterialTheme.typography.titleMedium, // 🔥 Đổi sang titleMedium cho đậm đà
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            val details = listOfNotNull(
                                transaction.movieTitle,
                                transaction.cinemaName,
                                transaction.showtime?.let { formatShowtime(it) }
                            ).joinToString(" • ")

                            if (details.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = details,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = formatTransactionDate(transaction.createdAt),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline // 🔥 Dùng màu outline để mờ hơn, phân tách rõ với nội dung
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$sign%,d".format(transaction.points), // 🔥 Format dấu phẩy hàng nghìn (VD: +1,500)
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = pointColor
                            )
                            // Chữ "điểm" nhỏ phía dưới hỗ trợ thị giác
                            Text(
                                text = "điểm",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = pointColor.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (index < state.transactions.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            // 🔥 Cập nhật lại thụt lề: 48dp (Icon) + 16dp (Spacer) + 4dp (Padding ngang) = 68dp
                            modifier = Modifier.padding(start = 68.dp)
                        )
                    }
                }
            }
        }
    }
}