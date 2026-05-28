package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.MovieDto
@Composable
fun MovieItem(
    movie: MovieDto, // Giả sử model của bạn đã có thêm thuộc tính ageRating (String) và rating (Double)
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)) // Giảm bo góc xuống một chút vì thẻ nhỏ hơn
            .clickable { onClick() },
        horizontalAlignment = Alignment.Start
    ) {
        // Khối Poster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = com.example.myapplication.R.drawable.empty),
                error = painterResource(id = com.example.myapplication.R.drawable.empty),
            )

            // Age Rating Badge (Gắn ở góc phải trên)
            // Kiểm tra nếu có dãn nhãn (vd: "T18", "C13", "K") thì mới hiển thị
            if (!movie.ageRating.isNullOrEmpty()) {
                val mappedRating = movie.ageRating.mapToVNAgeRating()
                val badgeColor = getAgeRatingColor(mappedRating)

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            color = badgeColor, // Dùng màu đã lấy theo logic
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = mappedRating,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Để chữ màu trắng cho nổi bật trên nền màu
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tên phim (Cho phép 2 dòng vì chiều ngang 3 cột rất hẹp)
        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelLarge, // Dùng size chữ nhỏ hơn so với Grid 2
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Hàng chứa Rating sao và Thời lượng
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween, // Đẩy 2 cụm ra 2 mép trái phải
            modifier = Modifier.fillMaxWidth()
        ) {
            // Cụm Rating (1 Sao + Điểm)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFFFFC107) // Màu vàng cam chuẩn của sao đánh giá
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = movie.rating.toString(), // vd: "8.5"
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Thời lượng (Bỏ icon đồng hồ đi cho đỡ chật, chỉ để chữ)
            Text(
                text = "${movie.durationMinutes}p",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


fun String.mapToVNAgeRating(): String {
    return when (this.uppercase().trim()) {
        "G", "PG" -> "P"        // Phổ biến
        "PG-13", "P13" -> "T13"        // Từ 13 tuổi
        "P16" -> "T16"
        "P18" -> "T18"
        "R", "NC-17" -> "T18"   // Từ 18 tuổi (Thường rạp VN xếp R vào T18)
        "P", "K", "T13", "T16", "T18", "C" -> this.uppercase().trim() // Chuẩn VN giữ nguyên
        else -> this.uppercase().trim() // Trả về gốc nếu mã lạ
    }
}

// Hàm gán màu theo từng nhãn độ tuổi
fun getAgeRatingColor(rating: String): Color {
    return when (rating) {
        "P" -> Color(0xFF4CAF50)   // Xanh lá (An toàn)
        "K" -> Color(0xFF2196F3)   // Xanh dương
        "T13" -> Color(0xFFFFC107) // Vàng
        "T16" -> Color(0xFFFF9800) // Cam
        "T18" -> Color(0xFFF44336) // Đỏ (Cảnh báo cao)
        "C" -> Color(0xFF212121)   // Đen/Xám đậm (Cấm chiếu)
        else -> Color(0xFF9E9E9E)  // Xám (Mặc định cho mã lạ)
    }
}