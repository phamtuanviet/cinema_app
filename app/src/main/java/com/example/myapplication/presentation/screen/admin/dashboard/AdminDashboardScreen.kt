package com.example.myapplication.presentation.screen.admin.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.PathEffect

import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.roundToInt

import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel



import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color

import androidx.compose.runtime.getValue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.data.remote.dto.RevenuePointDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToMore: () -> Unit,
    // THÊM CÁC CALLBACK ĐIỀU HƯỚNG
    onNavigateToUsers: () -> Unit,
    onNavigateToMovies: () -> Unit,
    onNavigateToCinemas: () -> Unit,
    onNavigateToShowtimes: () -> Unit,
    onNavigateToRevenue: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 1. Thẻ thống kê tổng quan (Grid 2x2)
                    item {
                        state.stats?.let { stats ->
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        title = "Tổng Users",
                                        value = stats.totalUsers.toString(),
                                        icon = Icons.Default.Person,
                                        color = Color(0xFF4CAF50),
                                        onClick = onNavigateToUsers // GẮN SỰ KIỆN CLICK
                                    )
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        title = "Tổng Phim",
                                        value = stats.totalMovies.toString(),
                                        icon = Icons.Default.Movie,
                                        color = Color(0xFF2196F3),
                                        onClick = onNavigateToMovies // GẮN SỰ KIỆN CLICK
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        title = "Tổng Rạp",
                                        value = stats.totalCinemas.toString(),
                                        icon = Icons.Default.Place,
                                        color = Color(0xFFFF9800),
                                        onClick = onNavigateToCinemas // GẮN SỰ KIỆN CLICK
                                    )
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        title = "Lịch Chiếu",
                                        value = stats.totalShowtimes.toString(),
                                        icon = Icons.Default.DateRange,
                                        color = Color(0xFF9C27B0),
                                        onClick = onNavigateToShowtimes // GẮN SỰ KIỆN CLICK
                                    )
                                }
                            }
                        }
                    }

                    // 2. Biểu đồ doanh thu 7 ngày qua
                    item {
                        Text(
                            text = "Doanh thu 7 ngày qua (VND)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        // Truyền callback click vào biểu đồ
                        RevenueLineChart(
                            data = state.revenueData,
                            onClick = onNavigateToRevenue
                        )
                    }
                }
            }
        }
    }
}
// ================= COMPONENT =================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit // THÊM THAM SỐ ONCLICK
) {
    // Sử dụng Card(onClick = ...) của Material 3 để có hiệu ứng nhấn (Ripple) đẹp mắt
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RevenueLineChart(data: List<RevenuePointDto>,
                     onClick: () -> Unit = {}) {
    if (data.isEmpty()) return

    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gradientColors = listOf(lineColor.copy(alpha = 0.4f), Color.Transparent)

    // Công cụ giúp tính toán kích thước và vẽ Text lên Canvas
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = labelColor)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp), // Tăng nhẹ chiều cao để có đủ chỗ cho trục X
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 1. Xác định không gian lề (Margin) cho các trục
            val leftMargin = 40.dp.toPx()   // Không gian bên trái cho Y-axis
            val bottomMargin = 24.dp.toPx() // Không gian bên dưới cho X-axis
            val topMargin = 16.dp.toPx()
            val rightMargin = 16.dp.toPx()

            // 2. Kích thước thực tế của vùng vẽ biểu đồ (Chart Area)
            val chartWidth = size.width - leftMargin - rightMargin
            val chartHeight = size.height - topMargin - bottomMargin

            val maxAmount = data.maxOfOrNull { it.amount } ?: 1.0
            val minAmount = data.minOfOrNull { it.amount } ?: 0.0
            val range = if (maxAmount == minAmount) maxAmount else (maxAmount - minAmount)

            val stepX = if (data.size > 1) chartWidth / (data.size - 1) else 0f

            // ==========================================
            // 3. VẼ TRỤC Y VÀ CÁC ĐƯỜNG KẺ NGANG (GRID)
            // ==========================================
            val ySteps = 4 // Chia làm 4 mốc giá trị trên trục Y
            for (i in 0..ySteps) {
                val yValue = minAmount + (range * i / ySteps)
                val yPos = topMargin + chartHeight - (chartHeight * i / ySteps)

                // Vẽ đường kẻ ngang (Dashed Line)
                drawLine(
                    color = gridColor,
                    start = Offset(leftMargin, yPos),
                    end = Offset(size.width - rightMargin, yPos),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // Rút gọn giá trị tiền tệ để hiển thị (VD: 1.500.000 -> 1.5M)
                val labelText = formatAmount(yValue)
                val textLayoutResult = textMeasurer.measure(text = labelText, style = labelStyle)

                // Vẽ Text cho trục Y
                drawText(
                    textMeasurer = textMeasurer,
                    text = labelText,
                    style = labelStyle,
                    topLeft = Offset(
                        x = leftMargin - textLayoutResult.size.width - 8.dp.toPx(),
                        y = yPos - (textLayoutResult.size.height / 2)
                    )
                )
            }

            // ==========================================
            // 4. VẼ ĐƯỜNG BIỂU ĐỒ (LINE), CHẤM TRÒN VÀ TRỤC X
            // ==========================================
            val path = Path()
            val fillPath = Path()

            data.forEachIndexed { index, point ->
                val x = leftMargin + index * stepX
                val y = topMargin + chartHeight - ((point.amount - minAmount) / range * chartHeight).toFloat()

                if (index == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }

                // Vẽ Text cho trục X (VD: Ngày "Th 2", "Th 3" hoặc "12/05")
                // TODO: Hãy thay "point.date" bằng tên biến ngày tháng trong DTO của bạn
                val xLabel = point.date
                val textLayoutResult = textMeasurer.measure(text = xLabel, style = labelStyle)
                drawText(
                    textMeasurer = textMeasurer,
                    text = xLabel,
                    style = labelStyle,
                    topLeft = Offset(
                        x = x - (textLayoutResult.size.width / 2),
                        y = size.height - bottomMargin + 4.dp.toPx()
                    )
                )

                // Vẽ chấm tròn tại mỗi điểm dữ liệu
                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            // Vẽ đường Line biểu đồ
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // ==========================================
            // 5. VẼ LỚP MÀU GRADIENT PHỦ BÊN DƯỚI
            // ==========================================
            fillPath.lineTo(leftMargin + chartWidth, topMargin + chartHeight)
            fillPath.lineTo(leftMargin, topMargin + chartHeight)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = topMargin,
                    endY = topMargin + chartHeight
                )
            )
        }
    }
}

// Hàm hỗ trợ format tiền tệ hiển thị trên trục Y cho gọn
// Ví dụ: 1,500,000 -> 1.5M | 200,000 -> 200K
fun formatAmount(amount: Double): String {
    return when {
        amount >= 1_000_000_000 -> "${(amount / 1_000_000_000).roundToInt()}B"
        amount >= 1_000_000 -> "${(amount / 1_000_000).roundToInt()}M"
        amount >= 1_000 -> "${(amount / 1_000).roundToInt()}K"
        else -> amount.roundToInt().toString()
    }
}