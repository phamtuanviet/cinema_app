package com.example.myapplication.presentation.component

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.dto.RevenueChartPoint

@Composable
fun AdvancedBarChart(
    data: List<RevenueChartPoint>,
    modifier: Modifier = Modifier,
    barColor: Color,
    gridColor: Color = Color.LightGray.copy(alpha = 0.5f)
) {
    // 1. Tính toán giá trị lớn nhất trục Y
    val maxValue = data.maxOfOrNull { it.value } ?: 1.0
    // Làm tròn max value lên số đẹp (VD: 4.2tr -> 5tr) để chia vạch cho chẵn
    val yAxisMax = if (maxValue == 0.0) 100.0 else (maxValue * 1.1)

    Canvas(modifier = modifier) {
        val yAxisWidth = 45.dp.toPx() // Chừa 45dp bên trái cho chữ trục Y
        val xAxisHeight = 24.dp.toPx() // Chừa 24dp bên dưới cho chữ trục X
        val chartWidth = size.width - yAxisWidth
        val chartHeight = size.height - xAxisHeight

        // Sơn chuẩn bị vẽ Text
        val textPaint = Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 10.dp.toPx()
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // 2. VẼ TRỤC Y VÀ ĐƯỜNG KẺ NGANG (LƯỚI)
        val ySteps = 4
        for (i in 0..ySteps) {
            val yPos = chartHeight - (chartHeight * i / ySteps)
            val value = (yAxisMax * i / ySteps)

            // Vẽ lưới nét đứt
            drawLine(
                color = gridColor,
                start = Offset(yAxisWidth, yPos),
                end = Offset(size.width, yPos),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // Vẽ chữ trục Y (Bên trái)
            drawIntoCanvas {
                textPaint.textAlign = Paint.Align.RIGHT
                it.nativeCanvas.drawText(
                    formatCompactCurrency(value),
                    yAxisWidth - 8.dp.toPx(), // Cách đường kẻ 8dp
                    yPos + 4.dp.toPx(), // Căn giữa theo chiều dọc vạch kẻ
                    textPaint
                )
            }
        }

        // 3. VẼ CỘT VÀ TRỤC X
        if (data.isNotEmpty()) {
            val barSpacing = chartWidth / data.size
            // Rộng tối đa 30dp để không lố, hoặc chiếm 60% khoảng trống
            val barWidth = (barSpacing * 0.6f).coerceAtMost(30.dp.toPx())

            data.forEachIndexed { index, point ->
                val barHeight = (point.value / yAxisMax).toFloat() * chartHeight
                val xOffset = yAxisWidth + (index * barSpacing) + (barSpacing - barWidth) / 2

                // Vẽ cột
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(xOffset, chartHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Vẽ chữ trục X (Chỉ vẽ 1 nửa số label nếu quá đông để tránh đè chữ)
                val shouldDrawLabel = data.size <= 15 || index % 2 == 0
                if (shouldDrawLabel) {
                    drawIntoCanvas {
                        textPaint.textAlign = Paint.Align.CENTER
                        it.nativeCanvas.drawText(
                            point.label,
                            xOffset + barWidth / 2,
                            size.height, // Xuống sát đáy
                            textPaint
                        )
                    }
                }
            }
        }
    }
}

// Hàm format tiền tỷ, triệu, nghìn cho gọn
fun formatCompactCurrency(value: Double): String {
    return when {
        value >= 1_000_000_000 -> String.format("%.1f Tỷ", value / 1_000_000_000).replace(".0", "")
        value >= 1_000_000 -> String.format("%.1f Tr", value / 1_000_000).replace(".0", "")
        value >= 1_000 -> String.format("%.0f K", value / 1_000)
        else -> value.toInt().toString()
    }
}