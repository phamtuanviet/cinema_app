package com.example.myapplication.presentation.component

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.BannerDto
import kotlinx.coroutines.delay

@Composable
fun BannerCarousel(
    banners: List<BannerDto>,
    onBannerClick: (bannerDto: BannerDto) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listSize = banners.size
    if (listSize == 0) return

    val fakePageCount = listSize * 1000
    val startIndex = fakePageCount / 2
    val initialPage = startIndex - (startIndex % listSize)

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { fakePageCount }
    )
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            if (!pagerState.isScrollInProgress) {
                if (pagerState.currentPage < fakePageCount - 1) {
                    // Bạn có thể giữ lại Log ở đây để test
                    Log.d("Banner", "Đang cuộn tới trang: ${pagerState.currentPage + 1}")
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
        ) { page ->
            val actualIndex = page % listSize
            val banner = banners[actualIndex]

            AsyncImage(
                model = banner.imageUrl,
                contentDescription = "Banner Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        when (banner.actionType.uppercase()) {
                            "URL" -> {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(banner.targetUrl)
                                )
                                context.startActivity(intent)
                            }
                            "MOVIE" -> onBannerClick(banner)
                        }
                    },
                placeholder = painterResource(id = com.example.myapplication.R.drawable.empty),
                error = painterResource(id = com.example.myapplication.R.drawable.empty),
            )
        }

        // Custom Indicator ở góc dưới trái
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(listSize) { index ->
                // 4. Cập nhật logic để Indicator sáng đúng chấm tương ứng
                val isSelected = (pagerState.currentPage % listSize) == index

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
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}