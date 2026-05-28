package com.example.myapplication.presentation.screen.cinema.cinema_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.MovieItemCard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinemaDetailScreen(
    cinemaId: String,
    onNavigateBack: () -> Unit, // 🔥 Thêm callback cho nút Quay lại
    onShowtimeClick: (showtimeId: String, movieId: String) -> Unit,
    viewModel: CinemaDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(cinemaId) {
        viewModel.loadData(cinemaId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Chuẩn MD3 Sáng/Tối
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chi tiết rạp",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Quay lại")
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
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Lỗi: ${state.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    // 🔥 Tên rạp được dời xuống đây
                    Text(
                        text = state.showtimes?.cinema?.name ?: "Rạp này hiện tại không có phim",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    val selectedTabIndex = state.dates.indexOf(state.selectedDate).coerceAtLeast(0)

                    if (state.dates.isNotEmpty()) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = MaterialTheme.colorScheme.surface, // Đổi màu nền thanh Tab
                            edgePadding = 8.dp,
                            indicator = { tabPositions ->
                                if (selectedTabIndex < tabPositions.size) {
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        color = MaterialTheme.colorScheme.primary, // Đổi màu thanh gạch dưới
                                        height = 3.dp
                                    )
                                }
                            },
                            divider = {
                                // Đường kẻ mờ dưới thanh Tab
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            }
                        ) {
                            state.dates.forEachIndexed { index, dateString ->
                                val isSelected = selectedTabIndex == index

                                // Xử lý chuỗi ngày định dạng "yyyy-MM-dd"
                                val parts = dateString.split("-")
                                val dayNumber = if (parts.size == 3) parts[2] else dateString
                                val monthNumber = if (parts.size == 3) parts[1] else ""
                                val subText = "Tháng $monthNumber"

                                Tab(
                                    selected = isSelected,
                                    onClick = { viewModel.loadShowtimes(cinemaId, dateString) },
                                    selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurface, // Chữ lúc không chọn (Trắng/Đen linh hoạt)
                                    text = {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = dayNumber,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = subText,
                                                fontSize = 12.sp,
                                                // Thay TextGray bằng onSurfaceVariant
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.showtimes?.movies?.let { movies ->
                            items(movies) { movie ->
                                MovieItemCard(movie = movie, onShowtimeClick = onShowtimeClick)
                            }
                        }
                    }
                }
            }
        }
    }
}