package com.example.myapplication.presentation.screen.movie.movie_list

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.BannerCarousel
import com.example.myapplication.presentation.component.MovieItem
import com.example.myapplication.presentation.component.MovieTabs

@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onNavigateBooking: (String) -> Unit
) {
    // Lấy state từ ViewModel
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // Hiển thị banner
        BannerCarousel(banners = state.banners, onMovieClick = onNavigateBooking)

        // Hiển thị các tab và xử lý thay đổi tab
        MovieTabs(
            selectedTab = state.selectedTab,
            onTabSelected = viewModel::changeTab
        )

        // Xử lý hiển thị loading, error hoặc danh sách phim
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "Unknown error",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    val movies = when (state.selectedTab) {
                        MovieTab.NOW_SHOWING -> state.nowShowing
                        MovieTab.COMING_SOON -> state.comingSoon
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // Chuyển xuống 2 cột để UI thoáng và hiện rõ poster hơn
                        contentPadding = PaddingValues(16.dp), // Padding tổng cho toàn bộ Grid
                        horizontalArrangement = Arrangement.spacedBy(16.dp), // Khoảng cách ngang giữa 2 cột
                        verticalArrangement = Arrangement.spacedBy(24.dp), // Khoảng cách dọc giữa các hàng
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(movies) { movie ->
                            MovieItem(
                                movie = movie,
                                onClick = { onNavigateBooking(movie.id) },
                                // Truyền fillMaxWidth để item tự động giãn full bề ngang của 1 cột
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}