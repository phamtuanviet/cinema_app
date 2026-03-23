package com.example.myapplication.presentation.component

import androidx.compose.foundation.Canvas
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

@Composable
fun ScreenIndicator() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                color = Color(0xFF1E88E5),
                style = Stroke(width = 4.dp.toPx())
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("MÀN HÌNH CHIẾU", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
    }
}