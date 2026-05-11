package com.example.myapplication.presentation.screen.admin.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*

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
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
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
                                        color = Color(0xFF4CAF50)
                                    )
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        title = "Tổng Phim",
                                        value = stats.totalMovies.toString(),
                                        icon = Icons.Default.Movie,
                                        color = Color(0xFF2196F3)
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
                                        color = Color(0xFFFF9800)
                                    )
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        title = "Lịch Chiếu",
                                        value = stats.totalShowtimes.toString(),
                                        icon = Icons.Default.DateRange,
                                        color = Color(0xFF9C27B0)
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
                        RevenueLineChart(data = state.revenueData)
                    }
                }
            }
        }
    }
}

// ================= COMPONENT =================

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
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
fun RevenueLineChart(data: List<RevenuePointDto>) {
    if (data.isEmpty()) return

    val lineColor = MaterialTheme.colorScheme.primary
    val gradientColors = listOf(lineColor.copy(alpha = 0.4f), Color.Transparent)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxAmount = data.maxOfOrNull { it.amount } ?: 1.0
                val minAmount = data.minOfOrNull { it.amount } ?: 0.0

                // Tránh chia cho 0 nếu min == max
                val range = if (maxAmount == minAmount) maxAmount else (maxAmount - minAmount)

                val width = size.width
                val height = size.height

                val stepX = width / (data.size - 1).coerceAtLeast(1)

                val path = Path()
                val fillPath = Path()

                data.forEachIndexed { index, point ->
                    val x = index * stepX
                    val y = height - ((point.amount - minAmount) / range * height * 0.8f).toFloat() - (height * 0.1f)

                    if (index == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }

                    // Vẽ các điểm (chấm tròn)
                    drawCircle(
                        color = lineColor,
                        radius = 6f,
                        center = Offset(x, y)
                    )
                }

                // Vẽ đường kẻ
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )

                // Fill gradient bên dưới đường kẻ
                fillPath.lineTo(width, height)
                fillPath.lineTo(0f, height)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = gradientColors,
                        startY = 0f,
                        endY = height
                    )
                )
            }
        }
    }
}