package com.example.myapplication.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.screen.movie.movie_list.MovieTab

@Composable
fun MovieTabs(
    selectedTab: MovieTab,
    onTabSelected: (MovieTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabIndex = if (selectedTab == MovieTab.NOW_SHOWING) 0 else 1

    TabRow(
        selectedTabIndex = tabIndex,
        modifier = modifier,
        containerColor = Color.Transparent, // Giúp tab chìm vào nền của màn hình
        divider = {}, // Xoá đường kẻ ngang mặc định thô cứng ở dưới
        indicator = { tabPositions ->
            // Custom thanh gạch chân (indicator)
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[tabIndex])
                    .height(4.dp)
                    .padding(horizontal = 32.dp) // Thu ngắn gạch chân lại để nhìn thanh thoát hơn
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)) // Bo tròn 2 góc trên
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    ) {
        // Gom data lại để lặp cho code gọn
        val tabs = listOf(
            MovieTab.NOW_SHOWING to "ĐANG CHIẾU",
            MovieTab.COMING_SOON to "SẮP CHIẾU"
        )

        tabs.forEachIndexed { _, (tab, title) ->
            val isSelected = selectedTab == tab

            // Hiệu ứng mờ/đậm màu chữ khi chuyển tab
            val textColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                },
                label = "tab_color_animation"
            )

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.padding(vertical = 4.dp),
                text = {
                    Text(
                        text = title,
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium,
                        // Chữ đậm lên khi được chọn, bình thường khi không chọn
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
        }
    }
}