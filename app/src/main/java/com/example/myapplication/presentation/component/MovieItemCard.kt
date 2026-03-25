package com.example.myapplication.presentation.component

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.MovieShowtimeDto
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.utils.openTab

val PrimaryRed = Color(0xFFE53935)
val BackgroundGray = Color(0xFFF5F5F5)
val TextGray = Color(0xFF757575)
val PinkAgeRating = Color(0xFFFFCDD2)
val PinkAgeRatingText = Color(0xFFD32F2F)

@Composable
fun MovieItemCard(
    movie: MovieShowtimeDto,
    onShowtimeClick: (showtimeId: String, movieId: String) -> Unit,
) {
    val context = LocalContext.current
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {

                // Poster Phim
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        onClick = {
                            // Kiểm tra an toàn: Chỉ mở tab nếu trailerUrl không null và không rỗng
                            if (!movie.trailerUrl.isNullOrEmpty()) {
                                openTab(context, movie.trailerUrl) // Gắn đúng tên hàm của bạn
                            } else {
                                // (Tùy chọn) Báo cho người dùng biết phim này chưa có trailer
                                Toast.makeText(
                                    context,
                                    "Phim này hiện chưa có trailer",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play Trailer",
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    // Tag HOT
                    Surface(
                        color = Color(0xFFFF9800),
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "HOT",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Thông tin phim
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.genres.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${movie.duration} phút",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Badge độ tuổi (VD: T18)
                    Surface(
                        color = PinkAgeRating,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = movie.ageRating,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = PinkAgeRatingText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Dòng chữ 2D Phụ đề
            Text(
                text = "2D Phụ đề",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Danh sách giờ chiếu
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movie.showtimes) { showtime ->
                    ShowtimePill(showtime, onClick = {
                        onShowtimeClick(showtime.id, movie.movieId)
                    })
                }
            }
        }
    }
}