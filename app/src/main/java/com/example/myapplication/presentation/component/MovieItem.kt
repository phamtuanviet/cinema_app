package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.MovieDto

@Composable
fun MovieItem(
    movie: MovieDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)) // Bo góc cho cả khối để hiệu ứng click (ripple) không bị tràn
            .clickable { onClick() },
        horizontalAlignment = Alignment.Start
    ) {
        // Khối Poster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f) // Tỷ lệ vàng chuẩn của poster phim (vd: 200x300)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant) // Màu nền chờ khi ảnh đang load
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop, // Cắt ảnh tràn viền, không bị méo
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tên phim
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis // Nếu tên quá dài sẽ tự hiện dấu "..."
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Thời lượng kèm Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Schedule, // Import icon đồng hồ
                contentDescription = "Duration",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${movie.durationMinutes} phút",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}