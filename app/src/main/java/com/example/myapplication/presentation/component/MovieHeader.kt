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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.MovieDto



@Composable
fun MovieHeader(movie: MovieDto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        // 1. Lớp dưới cùng: Ảnh nền Poster
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = "Poster of ${movie.title}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            // Nhớ import đúng R của project bạn nhé
            placeholder = painterResource(id = com.example.myapplication.R.drawable.empty),
            error = painterResource(id = com.example.myapplication.R.drawable.empty),
        )

        // 2. Lớp giữa: Gradient đổ bóng (Scrim)
        // Giữ nguyên Color.Black vì đây là kĩ thuật tạo độ tương phản vĩnh viễn trên nền ảnh
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        ),
                        startY = 300f
                    )
                )
        )

        // 3. Lớp trên cùng: Text
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            // Tên phim
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                // BẮT BUỘC giữ Color.White để hiển thị rõ trên nền Gradient đen
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hàng ngang chứa Age Rating và Thời lượng
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nhãn giới hạn độ tuổi (Badge)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    // CHUẨN MD3: Khai báo contentColor là onPrimary.
                    // Hệ thống sẽ tự tính toán màu sắc tốt nhất (thường là Trắng hoặc Đen)
                    // dựa trên màu primary của theme.
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = movie.ageRating,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        // Đã XÓA Color.White ở đây, Text sẽ kế thừa contentColor từ Surface
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Thời lượng và Ngôn ngữ
                Text(
                    text = "${movie.durationMinutes} phút • ${movie.language}",
                    style = MaterialTheme.typography.bodyMedium,
                    // Giữ nguyên màu trắng có alpha để tạo cấp bậc thông tin (nhạt hơn title)
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}