package com.example.myapplication.presentation.component

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.BannerDto
import kotlinx.coroutines.delay

@Composable
fun BannerCarousel(
    banners: List<BannerDto>,
    onMovieClick: (movieId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listSize = banners.size
    if (listSize == 0) return

    // Khởi tạo state cho Pager
    val pagerState = rememberPagerState(pageCount = { listSize })

    // Auto-scroll logic: Tự động chuyển trang mỗi 3 giây
    // Sẽ tạm dừng nếu người dùng đang vuốt tay (isScrollInProgress)
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % listSize
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = modifier.fillMaxWidth()
        .padding(16.dp)) {
        // HorizontalPager thay cho Box tĩnh
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
        ) { page ->
            val banner = banners[page]

            AsyncImage(
                model = banner.imageUrl,
                contentDescription = "Banner Image",
                contentScale = ContentScale.Crop, // Giúp ảnh lấp đầy khung hình không bị méo
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        when (banner.actionType.uppercase()) {
                            "URL" -> {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(banner.actionValue)
                                )
                                context.startActivity(intent)
                            }
                            "MOVIE" -> onMovieClick(banner.actionValue)
                        }
                    }
            )
        }

        // Custom Indicator ở góc dưới trái
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                // Thêm nền mờ đen để indicator luôn nổi bật không bị lặn vào ảnh
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(listSize) { index ->
                val isSelected = pagerState.currentPage == index

                // Hiệu ứng animation chiều rộng cho indicator
                val width by animateDpAsState(
                    targetValue = if (isSelected) 20.dp else 8.dp,
                    animationSpec = tween(durationMillis = 300),
                    label = "indicator_width"
                )

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            // Dùng màu primary của MaterialTheme khi được chọn
                            if (isSelected) MaterialTheme.colorScheme.primary
                            // Dùng màu nền mờ khi không được chọn
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}