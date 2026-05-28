package com.example.myapplication.presentation.screen.admin.movie.list

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myapplication.presentation.component.AdminMovieItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMovieListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit, // Truyền ID phim qua màn hình sửa
    viewModel: AdminMovieListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // Khi màn hình Resume (Bật lên lần đầu HOẶC Back từ màn khác về)
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadFirstPage()
            }
        }

        // Đăng ký lắng nghe
        lifecycleOwner.lifecycle.addObserver(observer)

        // Dọn dẹp khi Composable bị hủy
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // Nếu item đang hiển thị cuối cùng >= tổng số item hiện có - 1
            // (Nghĩa là chạm đáy)
            totalItems > 0 && lastVisibleItemIndex >= totalItems - 1
        }
    }


    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !state.isLoadingFirstPage && !state.isPaginating && !state.isLastPage) {
            viewModel.loadNextPage()
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            // 1. Nền Background để tách biệt với TopAppBar
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {

                // 2. TOP APP BAR
                TopAppBar(
                    title = {
                        Text(
                            text = "Quản lý Phim",
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

                // 3. THANH TÌM KIẾM
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Tìm kiếm phim...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.onSearchQueryChange("")
                                focusManager.clearFocus()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Xóa")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(100), // Bo tròn dạng viên thuốc
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                // 4. ĐƯỜNG KẺ ĐÁY (Vì không có TabRow nên chốt luôn ở đây)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.prepareForReturn()
                    onNavigateToCreate()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                // Sửa màu Icon thành onPrimary cho đồng bộ
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm phim mới",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ){ paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                // 🔥 4. Bắt sự kiện bấm ra khoảng trống thì cất bàn phím
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            when {
                state.isLoadingFirstPage -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null && state.movies.isEmpty() -> {
                    // Lỗi và chưa có data
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadFirstPage() }) { Text("Thử lại") }
                    }
                }
                state.movies.isEmpty() -> {
                    Text(
                        "Không tìm thấy phim nào.",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState, // 🔥 4. Nhớ gắn listState vào LazyColumn
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(state.movies) { index, movie ->
                            AdminMovieItem(
                                movie = movie,
                                onEditClick = {
                                    viewModel.prepareForReturn()
                                    onNavigateToEdit(movie.id) }
                            )

                            // 🔥 5. XÓA BỎ HOÀN TOÀN ĐOẠN IF (index == lastIndex) CŨ Ở ĐÂY
                        }

                        // Hiển thị vòng xoay nhỏ ở dưới cùng khi đang load trang tiếp theo
                        if (state.isPaginating) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }

                        if (state.error != null && !state.isLoadingFirstPage) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "Lỗi: ${state.error}", color = MaterialTheme.colorScheme.error)
                                    TextButton(onClick = { viewModel.loadNextPage() }) {
                                        Text("Thử lại")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

