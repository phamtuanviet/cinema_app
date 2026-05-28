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
import com.example.myapplication.data.remote.enums.PostType
import com.example.myapplication.presentation.component.PromotionContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionDetailScreen(
    postId: String,
    onNavigateToVoucher: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PromotionDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()


    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    val titleText = when (state.post?.type) {
                        PostType.VOUCHER -> "Chi tiết Voucher"
                        null -> "Đang tải..."
                        else -> "Chi tiết bài viết"
                    }
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Vẫn dùng AutoMirrored để hỗ trợ đa ngôn ngữ RTL tốt hơn
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Nền Primary chuẩn
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ){ paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                        onNavigateToVoucher = onNavigateToVoucher,
                    )
                }
            }
        }
    }
}