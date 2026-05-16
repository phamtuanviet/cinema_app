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

import android.content.Intent
import android.net.Uri

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.platform.LocalContext


@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onNavigateBooking: (String) -> Unit,
    onNavigateToMovieDetail: (String) -> Unit // Thêm callback này
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current // Để dùng cho Intent mở URL

    Column(modifier = Modifier.fillMaxSize()) {

        // Hiển thị banner và xử lý click phân nhánh
        BannerCarousel(
            banners = state.banners,
            onBannerClick = { banner ->
                when (banner.actionType) {
                    "MOVIE" -> {
                        // Di chuyển đến màn chi tiết phim
                        banner.movieId?.let { onNavigateToMovieDetail(it) }
                    }
                    "URL" -> {
                        // Mở trình duyệt web
                        banner.targetUrl?.let { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    }
                }
            }
        )

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
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(movies) { movie ->
                            MovieItem(
                                movie = movie,
                                onClick = { onNavigateBooking(movie.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}