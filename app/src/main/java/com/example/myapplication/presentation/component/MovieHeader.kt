package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Lưu ý: Cần import Coil
import com.example.myapplication.data.remote.dto.MovieDto

@Composable
fun MovieHeader(movie: MovieDto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // Bạn có thể tùy chỉnh chiều cao này
    ) {
        // 1. Lớp dưới cùng: Ảnh nền Poster
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = "Poster of ${movie.title}",
            contentScale = ContentScale.Crop, // Cắt ảnh để lấp đầy Box
            modifier = Modifier.fillMaxSize()
        )

        // 2. Lớp giữa: Gradient đổ bóng từ dưới lên
        // Giúp chữ màu trắng luôn nổi bật dù poster có màu sáng
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent, // Trong suốt ở trên
                            Color.Black.copy(alpha = 0.85f) // Đen mờ ở dưới
                        ),
                        startY = 300f // Bắt đầu đổ bóng từ giữa ảnh trở xuống
                    )
                )
        )

        // 3. Lớp trên cùng: Text (Tên phim, Nhãn tuổi, Thời lượng)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart) // Ép toàn bộ cụm text xuống góc dưới cùng bên trái
                .padding(16.dp)
        ) {
            // Tên phim
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White // Dùng màu trắng cố định vì nền đã là gradient đen
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hàng ngang chứa Age Rating và Thời lượng
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nhãn giới hạn độ tuổi (Badge)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary, // Đổi màu này theo brand của bạn
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = movie.ageRating,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Thời lượng và Ngôn ngữ
                Text(
                    text = "${movie.durationMinutes} phút • ${movie.language}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f) // Màu trắng hơi mờ đi một chút cho đẹp
                )
            }
        }
    }
}