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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onNavigateBooking: (String) -> Unit,
    onNavigateToMovieDetail: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()

    val interaction by listState.interactionSource.interactions.collectAsState(initial = null)

    LaunchedEffect(interaction) {
        if (interaction is DragInteraction.Start) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                val totalRows = (state.movies.size + 2) / 3
                if (lastVisibleIndex != null && lastVisibleIndex >= totalRows - 1 && !state.isLoading) {
                    viewModel.loadNextPage()
                }
            }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Phim chiếu rạp",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Nền Primary đè lên Status Bar
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }

        ) {
            // 🔥 Lưới luôn luôn tồn tại, không bị ẩn đi bởi state.isLoading nữa
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxSize()
            ){
                // 1. BANNER
                if (state.searchQuery.isBlank()) {
                    item(key = "banner") {
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
                    }
                }

                // 2. THANH SEARCH
                stickyHeader(key = "sticky_search_tabs") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            // BẮT BUỘC phải có màu nền đục, nếu không phim bên dưới sẽ cuộn xuyên thấu qua
                            .background(MaterialTheme.colorScheme.background)
                            .padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = state.searchQuery,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = { focusManager.clearFocus() }
                            ),
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Tìm kiếm tên phim...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            trailingIcon = {
                                if (state.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        viewModel.onSearchQueryChanged("")
                                        focusManager.clearFocus()
                                    }) { Icon(Icons.Default.Clear, contentDescription = "Clear") }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        MovieTabs(
                            selectedTab = state.selectedTab,
                            onTabSelected = viewModel::changeTab
                        )
                    }
                }

                // 4. XỬ LÝ TRẠNG THÁI HIỂN THỊ NỘI DUNG PHIM
                when {
                    // Chỉ hiện Loading bự khi load lần đầu hoặc đổi tab (danh sách phim chưa có gì)
                    state.isLoading && state.movies.isEmpty() -> {
                        item(key = "loading") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp), // Chiếm một khoảng trống để hiện loading
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    state.error != null && state.movies.isEmpty() -> {
                        item(key = "error") {
                            Text(
                                text = state.error ?: "Lỗi không xác định",
                                modifier = Modifier.padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    state.movies.isEmpty() && !state.isLoading -> {
                        item(key = "empty") {
                            Text(
                                text = "Không tìm thấy phim nào phù hợp.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        // In ra danh sách phim thực tế
                        items(
                            items = state.movies.chunked(3), // Gộp 3 phim thành 1 mảng nhỏ
                            key = { rowMovies -> "row_${rowMovies.first().id}" }
                        ) { rowMovies ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Render từng phim trong hàng
                                rowMovies.forEach { movie ->
                                    MovieItem(
                                        movie = movie,
                                        onClick = { onNavigateBooking(movie.id) },
                                        modifier = Modifier.weight(1f) // Ép chia đều không gian
                                    )
                                }

                                // Xử lý hàng cuối cùng nếu bị lẻ (1 hoặc 2 phim)
                                val emptySlots = 3 - rowMovies.size
                                repeat(emptySlots) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        // 🔥 Hiện vòng xoay xoay ở DƯỚI CÙNG danh sách khi đang load thêm trang
                        if (state.isFetchingMore) {
                            // Sửa thành maxLineSpan để tương thích với mọi số lượng cột (đặc biệt khi dùng 3 cột)
                            item(key = "fetching_more") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}