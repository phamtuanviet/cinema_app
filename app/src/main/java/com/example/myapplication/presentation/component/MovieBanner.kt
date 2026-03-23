package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.MovieDto

@Composable
fun MovieBanner(
    movie: MovieDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp) // Tăng chiều cao lên một chút để banner nhìn "đã" hơn
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)) // Bo tròn 2 góc dưới nếu thích
    ) {
        // 🖼 1. Ảnh Poster làm nền
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🌫 2. Lớp phủ Gradient mờ dần từ giữa xuống đáy
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.9f) // Đen đậm ở dưới đáy
                        ),
                        startY = 300f // Bắt đầu phủ gradient từ khoảng giữa ảnh
                    )
                )
        )

        // 📝 3. Nội dung phim (Dời xuống góc dưới cùng bên trái)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            // Tên phim
            Text(
                text = movie.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            // Thêm một số thông tin phụ cho giống app rạp chiếu (Dùng data từ MovieDto cũ của bạn)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Badge giới hạn độ tuổi
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.error, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = movie.ageRating,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${movie.durationMinutes} phút • ${movie.genres.take(2).joinToString(", ")}",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(16.dp))

            // 🖱 4. Nút bấm sử dụng Material3
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Đổi màu nhấn của app
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(50), // Bo tròn thành hình viên thuốc
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Chi tiết phim",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}