package com.example.myapplication.presentation.screen.admin.news.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.AdminNewsDto
import com.example.myapplication.data.remote.dto.NewsTypeTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNewsListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: AdminNewsListViewModel = hiltViewModel()
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
            // 1. Nền Background
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {

                // 2. TOP APP BAR
                TopAppBar(
                    title = {
                        Text(
                            text = "Quản lý Bài viết & Sự kiện",
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
                val hintText = if (state.currentTab == NewsTypeTab.NORMAL) "Tìm theo Tiêu đề bài viết..." else "Tìm Tiêu đề hoặc Mã Voucher..."
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text(hintText) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange(""); focusManager.clearFocus() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Xóa")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
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

                // 4. THANH TAB
                val tabs = NewsTypeTab.values()
                val selectedTabIndex = tabs.indexOf(state.currentTab)

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = MaterialTheme.colorScheme.primary,
                                height = 3.dp // Thanh chỉ báo đậm hơn
                            )
                        }
                    },
                    divider = {} // Ẩn divider mặc định
                ) {
                    tabs.forEach { tab ->
                        val isSelected = state.currentTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onTabSelected(tab)
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }

                // 5. ĐƯỜNG KẺ ĐÁY
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
                // Đổi màu Icon dấu cộng sang chuẩn onPrimary
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm Bài viết",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            when {
                state.isLoadingFirstPage -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.error != null && state.newsList.isEmpty() -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadFirstPage() }) { Text("Thử lại") }
                    }
                }
                state.newsList.isEmpty() -> {
                    Text("Không tìm thấy bài viết nào.", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.newsList, key = { it.id }) { newsItem ->
                            AdminNewsItem(
                                news = newsItem,
                                onEditClick = {
                                    viewModel.prepareForReturn()
                                    onNavigateToEdit(newsItem.id) }
                            )
                        }
                        if (state.isPaginating) {
                            item {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= ITEM VIEW CỦA TIN TỨC =================

@Composable
fun AdminNewsItem(
    news: AdminNewsDto,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh Thumbnail
            AsyncImage(
                model = news.thumbnailUrl ?: "https://via.placeholder.com/150",
                contentDescription = news.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 100.dp, height = 75.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Nội dung
            Column(modifier = Modifier.weight(1f)) {

                // Trạng thái Xuất bản
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                    val statusColor = if (news.published) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                    val statusText = if (news.published) "Đã xuất bản" else "Bản nháp"

                    Icon(Icons.Default.Circle, contentDescription = null, tint = statusColor, modifier = Modifier.size(8.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(statusText, style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Nếu là bài VOUCHER, hiển thị Badge Mã Code
                if (news.type == "VOUCHER" && !news.voucherCode.isNullOrEmpty()) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(4.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mã: ${news.voucherCode}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Nếu là bài NORMAL, hiển thị ngày bắt đầu
                    Text(
                        text = "Từ: ${news.startDate ?: "Ngay lập tức"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Nút Chỉnh sửa
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Sửa Bài",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}