package com.example.myapplication.presentation.screen.chatbot

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    val focusManager = LocalFocusManager.current

    // 1. LAUNCHER XỬ LÝ XIN QUYỀN VỊ TRÍ
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        if (isGranted) {
            // Gọi hàm trong ViewModel để lấy tọa độ thực tế và gửi cho Bot
            viewModel.fetchLocationAndSend()
        }
    }

    // Tự động cuộn xuống tin nhắn cuối cùng
    val messagesSize = uiState.messages.size
    LaunchedEffect(messagesSize) {
        if (messagesSize > 0) {
            listState.animateScrollToItem(messagesSize - 1)
        }
    }

    // Logic load more history
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            firstVisibleItem <= 2
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
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        bottomBar = {
            ChatInputBar(
                isSending = uiState.isSending,
                onSendMessage = { text ->
                    viewModel.sendMessage(text)
                    focusManager.clearFocus()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            LazyColumn(
                state = listState,
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

                // 2. CẬP NHẬT GỌI CHATMESSAGBUBBLE TẠI ĐÂY
                items(uiState.messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        navController = internalNavController, // Truyền trực tiếp NavController vào
                        onLocationRequest = {
                            // Kích hoạt Launcher xin quyền khi user bấm nút "Chia sẻ vị trí"
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }

                // Hiển thị trạng thái Bot đang trả lời (Typing...)
                if (uiState.isSending) {
                    item {
                        Text(
                            text = "Bot đang suy nghĩ...",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}