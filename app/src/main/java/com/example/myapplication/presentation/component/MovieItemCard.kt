package com.example.myapplication.presentation.component

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
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
import androidx.compose.runtime.Composable


@Composable
fun MovieItemCard(
    movie: MovieShowtimeDto,
    onShowtimeClick: (showtimeId: String, movieId: String) -> Unit,
) {
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        // 🔥 Nền Card tự động đổi Sáng (Trắng) / Tối (Xám đậm)
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {

                // --- 1. POSTER PHIM ---
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

                    // Nút Play Trailer (Giữ nguyên overlay đen mờ vì nó nằm trên ảnh)
                    Surface(
                        onClick = {
                            if (!movie.trailerUrl.isNullOrEmpty()) {
                                openTab(context, movie.trailerUrl)
                            } else {
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

                    // Tag HOT: Dùng màu error (Đỏ/Cam) của MD3 để luôn nổi bật
                    Surface(
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "HOT",
                            color = MaterialTheme.colorScheme.onError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // --- 2. THÔNG TIN PHIM ---
                Column(modifier = Modifier.weight(1f)) {
                    // Tên phim: Đổi Color.Black -> onSurface
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Thể loại: Đổi TextGray -> onSurfaceVariant
                    Text(
                        text = movie.genres.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Thời lượng
                    Text(
                        text = "${movie.duration} phút",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Badge độ tuổi (VD: T18)
                    // 🔥 Thay thế cụm màu Pink fix cứng bằng errorContainer / onErrorContainer của MD3
                    // (Hoặc bạn có thể dùng hàm getAgeRatingColor() đã tạo ở bước trước nếu muốn)
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = movie.ageRating,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Dòng chữ định dạng (VD: 2D Phụ đề)
            Text(
                text = "2D Phụ đề",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // Đổi Color.Black -> onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Danh sách giờ chiếu
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movie.showtimes) { showtime ->
                    // Lưu ý: Đảm bảo bên trong component ShowtimePill bạn cũng đang dùng MaterialTheme nhé!
                    ShowtimePill(showtime, onClick = {
                        onShowtimeClick(showtime.id, movie.movieId)
                    })
                }
            }
        }
    }
}