package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.MovieChatbotResponse

@Composable
fun MovieCarouselWidget(movies: List<MovieChatbotResponse>, onClick: (String) -> Unit) {
    if (movies.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
    ) {
        items(movies) { movie ->
            ElevatedCard(
                modifier = Modifier
                    .width(150.dp) // Nhỉnh hơn một chút để chứa được nhiều chữ hơn
                    .clickable { movie.movieId?.let { onClick(it) } },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    // --- 1. KHỐI ẢNH POSTER VÀ BADGE ---
                    Box(modifier = Modifier.fillMaxWidth().height(210.dp)) {
                        // Poster phim
                        AsyncImage(
                            model = movie.posterUrl,
                            contentDescription = movie.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // 🔥 Overlay Độ tuổi (Góc trên trái)
                        if (!movie.ageRating.isNullOrBlank()) {
                            // Tuỳ biến màu sắc: Phim 18+ thì màu Đỏ, còn lại màu Cam/Primary
                            val isAdult = movie.ageRating.contains("18")
                            val badgeColor = if (isAdult) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary

                            Surface(
                                color = badgeColor,
                                shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 12.dp),
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = movie.ageRating,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // 🔥 Overlay Điểm số (Góc dưới phải)
                        if (movie.averageRating != null && movie.averageRating > 0) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.75f), // Nền đen mờ 75%
                                shape = RoundedCornerShape(topStart = 12.dp),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFC107), // Vàng óng
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.1f", movie.averageRating),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // --- 2. KHỐI THÔNG TIN BÊN DƯỚI CHIẾU ---
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        // Tên phim
                        Text(
                            text = movie.title ?: "Đang cập nhật",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // 🔥 Chi tiết: Thời lượng • Thể loại
                        val details = listOfNotNull(
                            movie.durationMinutes?.let { "${it} phút" },
                            movie.genres?.firstOrNull() // Chỉ lấy 1 thể loại đầu tiên cho khỏi bị tràn
                        ).joinToString(" • ")

                        Text(
                            text = details.ifEmpty { "Đang cập nhật" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}