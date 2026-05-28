package com.example.myapplication.presentation.screen.admin.revenue


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.remote.dto.RevenueChartPoint
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import com.example.myapplication.presentation.component.AdvancedBarChart


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRevenueScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminRevenueViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thống kê Doanh Thu",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. THANH LỌC THỜI GIAN (DAY, WEEK, MONTH)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(TimeRangeFilter.values()) { filter ->
                    FilterChip(
                        selected = state.selectedFilter == filter,
                        onClick = { viewModel.onFilterSelected(filter) },
                        label = { Text(filter.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousPeriod() }) {
                    Icon(Icons.Rounded.ChevronLeft, contentDescription = "Trở về trước")
                }

                Text(
                    text = formatCurrentPeriod(state.selectedFilter, state.currentDate),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = { viewModel.nextPeriod() },
                    enabled = !isFuture(state.selectedFilter, state.currentDate) // K cho xem tương lai
                ) {
                    Icon(Icons.Rounded.ChevronRight, contentDescription = "Tiếp theo")
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                state.summary?.let { summary ->
                    // 2. THẺ TỔNG QUAN
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RevenueSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Tổng doanh thu",
                            value = formatCurrency(summary.totalRevenue),
                            icon = Icons.Rounded.AttachMoney,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        RevenueSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Giao dịch (TC)",
                            value = "${summary.totalSuccessfulTransactions}",
                            icon = Icons.Rounded.ReceiptLong,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    // 3. BIỂU ĐỒ CỘT (TỰ VẼ BẰNG CANVAS)
                    Text(
                        text = "Biểu đồ theo thời gian",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(260.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        if (summary.chartData.isNotEmpty()) {
                            AdvancedBarChart(
                                data = summary.chartData,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                barColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Không có dữ liệu", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun RevenueSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = contentColor.copy(alpha = 0.8f))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = contentColor)
        }
    }
}

// BIỂU ĐỒ CỘT CUSTOM SIÊU NHẸ MÀ ĐẸP
@Composable
fun SimpleBarChart(
    data: List<RevenueChartPoint>,
    modifier: Modifier = Modifier,
    barColor: Color
) {
    val maxRevenue = data.maxOfOrNull { it.value } ?: 1.0

    Canvas(modifier = modifier) {
        val barWidth = size.width / (data.size * 2f)
        val maxBarHeight = size.height - 40.dp.toPx() // Chừa chỗ cho Text ở dưới

        data.forEachIndexed { index, point ->
            val barHeight = (point.value / maxRevenue).toFloat() * maxBarHeight
            val startOffset = index * (size.width / data.size) + (size.width / data.size - barWidth) / 2

            // Vẽ cột
            drawRoundRect(
                color = barColor,
                topLeft = Offset(startOffset, maxBarHeight - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Vẽ chữ (Label) ở dưới
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 12.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                canvas.nativeCanvas.drawText(
                    point.label,
                    startOffset + barWidth / 2,
                    size.height,
                    paint
                )
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount)
}