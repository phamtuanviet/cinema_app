package com.example.myapplication.presentation.screen.chatbot

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.presentation.component.ChatInputBar
import com.example.myapplication.presentation.component.ChatMessageBubble


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    internalNavController: NavController,
    rootNavController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Quản lý Focus để ẩn bàn phím
    val focusManager = LocalFocusManager.current

    // Tự động cuộn xuống tin nhắn cuối cùng khi có tin nhắn mới
    val messagesSize = uiState.messages.size
    LaunchedEffect(messagesSize) {
        if (messagesSize > 0) {
            listState.animateScrollToItem(messagesSize - 1)
        }
    }

    // Logic cuộn lên để load thêm (giữ nguyên)
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            firstVisibleItem <= 2 // Gần chạm nóc trên cùng thì load thêm
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !uiState.isLoadingHistory && !uiState.isLastPage) {
            viewModel.loadMoreMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trợ lý rạp phim") },
                navigationIcon = {
                    IconButton(onClick = { internalNavController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearSession() }) {
                        Icon(Icons.Default.Delete, contentDescription = "New Chat")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        // ĐẶT THANH NHẬP CHỮ VÀO BOTTOM BAR ĐỂ NÓ LUÔN BÁM ĐÁY MÀN HÌNH
        bottomBar = {
            ChatInputBar(
                isSending = uiState.isSending,
                onSendMessage = { text ->
                    viewModel.sendMessage(text)
                    // Gửi xong thì ẩn bàn phím luôn
                    focusManager.clearFocus()
                }
            )
        }
    ) { paddingValues ->

        // Cột chứa danh sách tin nhắn
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // THÊM SỰ KIỆN: Chạm vào vùng trống sẽ ẩn bàn phím
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            LazyColumn(
                state = listState,
                // TẮT REVERSE ĐỂ HIỂN THỊ TỪ TRÊN XUỐNG DƯỚI
                reverseLayout = false,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.isLoadingHistory) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }

                items(uiState.messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        onMovieClick = { id -> internalNavController.navigate("movie_detail/$id") },
                        onBookTicketClick = { id -> internalNavController.navigate("booking/$id") }
                    )
                }
            }
        }
    }
}