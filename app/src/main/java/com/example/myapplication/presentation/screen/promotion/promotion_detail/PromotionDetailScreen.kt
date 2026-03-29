package com.example.myapplication.presentation.screen.promotion.promotion_detail


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.presentation.component.PromotionContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionDetailScreen(
    postId: String,
    onNavigateToVoucher: (String) -> Unit,
    viewModel: PromotionDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Hiệu ứng cuộn cho TopAppBar chuẩn Material 3
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Chi tiết ưu đãi") },
                // BỎ GIAO DIỆN NÚT BACK Ở ĐÂY
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> {
                    Text(
                        text = "Đã xảy ra lỗi: ${state.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                state.post != null -> {
                    PromotionContent(
                        post = state.post!!,
                        onNavigateToVoucher = onNavigateToVoucher
                    )
                }
            }
        }
    }
}