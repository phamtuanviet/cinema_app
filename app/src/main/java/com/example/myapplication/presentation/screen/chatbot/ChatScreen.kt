package com.example.myapplication.presentation.screen.chatbot

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.presentation.component.ChatInputBar
import com.example.myapplication.presentation.component.ChatMessageBubble
import com.example.myapplication.presentation.component.TypingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    internalNavController: NavController,
    rootNavController: NavController // Giữ nguyên parameter của bạn
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
            viewModel.fetchLocationAndSend()
        }
    }

    // Tự động cuộn xuống tin nhắn cuối cùng (kể cả khi user nhắn hoặc bot trả lời)
    val messagesSize = uiState.messages.size
    LaunchedEffect(messagesSize, uiState.isSending) {
        if (messagesSize > 0) {
            // Cuộn mượt mà đến phần tử cuối cùng
            listState.animateScrollToItem(
                index = if (uiState.isSending) messagesSize else messagesSize - 1
            )
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
                title = {
                    Text("Trợ lý rạp phim", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { internalNavController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                // 🔥 ĐÃ XÓA windowInsets = WindowInsets(0.dp) để nó ăn lên thanh WiFi
                actions = {
                    IconButton(onClick = { viewModel.clearSession() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa đoạn chat")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // 🔥 Đưa về màu Primary để đồng bộ với toàn bộ App
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                isSending = uiState.isSending,
                onSendMessage = { text ->
                    viewModel.sendMessage(text)
                }
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
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.isLoadingHistory) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // 2. RENDER MESSAGES (Tin nhắn người dùng bên phải, Bot bên trái do ChatMessageBubble quyết định)
                items(uiState.messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        navController = internalNavController,
                        onLocationRequest = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }

                // 3. UI BOT ĐANG TRẢ LỜI (Hiển thị bên trái)
                if (uiState.isSending) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 64.dp), // Ép sang trái (giữ nguyên khoảng trống bên phải)
                            horizontalArrangement = Arrangement.Start // Căn lề trái
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = 4.dp,
                                    bottomEnd = 16.dp
                                ),
                                // Dùng surfaceVariant tạo nền màu xám nhạt nhẹ nhàng
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                TypingIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}