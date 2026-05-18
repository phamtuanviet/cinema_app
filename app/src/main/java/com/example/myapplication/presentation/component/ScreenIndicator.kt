package com.example.myapplication.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.unit.dp
import kotlin.io.path.moveTo
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

object SeatColors {
    // 1. Ghế trống (Available):
    // Dùng secondaryContainer để ghế trống nhìn sáng sủa, sạch sẽ và có sức sống hơn
    // (nó sẽ có một chút sắc độ của màu theme thay vì chỉ là màu xám xịt).
    val available: Color
        @Composable get() = MaterialTheme.colorScheme.secondaryContainer

    // 2. Ghế bạn đang chọn (Selected):
    val selected: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    // 3. Ghế người khác đang giữ (Held by other):
    val heldByOther: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFFB74D) else Color(0xFFFF9800)

    // 4. Ghế đã bán (Booked):
    // Để ghế trông có vẻ "đã chết" và không thể click, ta dùng chính màu viền (outline)
    // hoặc một màu xám tĩnh có độ tương phản cao với nền.
    val booked: Color
        @Composable get() = if (isSystemInDarkTheme()) {
            Color(0xFF333333) // Xám rất tối (chìm vào nền đen)
        } else {
            Color(0xFFBDBDBD) // Xám đục (phân biệt rõ với secondaryContainer sáng)
        }
}
// ==============================
// 2. MÀN HÌNH CHIẾU
// ==============================
@Composable
fun ScreenIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Lấy màu primary từ theme cho đường cong
        val strokeColor = MaterialTheme.colorScheme.primary

        Canvas(modifier = Modifier.width(260.dp).height(20.dp)) {
            val path = Path().apply {
                moveTo(0f, size.height)
                quadraticBezierTo(
                    size.width / 2, 0f, // Điểm uốn ở giữa kéo lên trên
                    size.width, size.height
                )
            }
            drawPath(
                path = path,
                color = strokeColor, // Thay thế Color(0xFF1E88E5)
                style = Stroke(width = 4.dp.toPx())
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "MÀN HÌNH CHIẾU",
            style = MaterialTheme.typography.labelMedium,
            // Dùng onSurfaceVariant thay cho Color.Gray để nhìn sang trọng hơn
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}