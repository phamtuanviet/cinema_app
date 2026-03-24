package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Nhớ thêm thư viện Coil vào build.gradle nhé
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.ShowtimeDetailDto

@Composable
fun MovieHeaderOption(
    movie: MovieDto?,
    showtime: ShowtimeDetailDto?,
    modifier: Modifier = Modifier
) {
    // Nếu data chưa load xong thì không hiển thị hoặc có thể làm Shimmer loading ở đây
    if (movie == null || showtime == null) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 🖼 Poster phim bên trái
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = "Poster ${movie.title}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(100.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp)),
            placeholder = painterResource(id = com.example.myapplication.R.drawable.empty),
            error = painterResource(id = com.example.myapplication.R.drawable.empty),
        )

        // 📝 Thông tin phim & suất chiếu bên phải
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tên phim (in đậm, tối đa 2 dòng)
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Cụm rạp & Phòng chiếu
            Text(
                text = "${showtime.cinemaName} - ${showtime.room}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Giờ & Ngày chiếu (Nổi bật lên một chút)
            Text(
                text = "${showtime.time} | ${showtime.date}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            // Các thông tin phụ: Nhãn tuổi, Thời lượng, Ngôn ngữ
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Badge giới hạn độ tuổi (ví dụ: T18, C13...)
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = movie.ageRating,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "• ${movie.durationMinutes} phút • ${movie.language}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}