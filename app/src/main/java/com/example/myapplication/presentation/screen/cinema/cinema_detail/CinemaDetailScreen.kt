package com.example.myapplication.presentation.screen.cinema.cinema_detail

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.MovieItemCard

// Bảng màu Custom
val PrimaryRed = Color(0xFFE53935)
val BackgroundGray = Color(0xFFF5F5F5)
val TextGray = Color(0xFF757575)
val PinkAgeRating = Color(0xFFFFCDD2)
val PinkAgeRatingText = Color(0xFFD32F2F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinemaDetailScreen(
    cinemaId: String,
    onShowtimeClick: (showtimeId: String, movieId: String) -> Unit,
    viewModel: CinemaDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(cinemaId) {
        viewModel.loadData(cinemaId)
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = state.showtimes?.cinema?.name ?: "BETA MỸ ĐÌNH",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryRed)
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Lỗi: ${state.error}", color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

                    val selectedTabIndex = state.dates.indexOf(state.selectedDate).coerceAtLeast(0)

                    if (state.dates.isNotEmpty()) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.White,
                            edgePadding = 8.dp,
                            indicator = { tabPositions ->
                                if (selectedTabIndex < tabPositions.size) {
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        color = PrimaryRed,
                                        height = 3.dp
                                    )
                                }
                            },
                            divider = {}
                        ) {

                            state.dates.forEachIndexed { index, dateString ->
                                Log.d("CheckDate", dateString) // VD: "2026-03-26"
                                val isSelected = selectedTabIndex == index

                                // Xử lý chuỗi ngày định dạng "yyyy-MM-dd"
                                val parts = dateString.split("-")
                                // Đảm bảo chuỗi có đủ 3 phần (Năm, Tháng, Ngày)
                                val dayNumber = if (parts.size == 3) parts[2] else dateString
                                val monthNumber = if (parts.size == 3) parts[1] else ""

                                val subText = "Tháng $monthNumber"

                                Tab(
                                    selected = isSelected,
                                    onClick = { viewModel.loadShowtimes(cinemaId, dateString) },
                                    selectedContentColor = PrimaryRed,
                                    unselectedContentColor = Color.Black,
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
                                                color = if (isSelected) PrimaryRed else TextGray
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
                                MovieItemCard(movie = movie,onShowtimeClick = onShowtimeClick)
                            }
                        }
                    }
                }
            }
        }
    }
}