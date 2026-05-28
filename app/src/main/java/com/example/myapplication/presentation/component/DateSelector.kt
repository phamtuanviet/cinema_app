package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.utils.formatDateDisplay
import com.example.myapplication.utils.getFormattedDateInfo

@Composable
fun DateSelector(
    dates: List<String>,
    selected: String?,
    isLoading: Boolean,
    onClick: (String) -> Unit
) {
    LazyRow(

        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),

        modifier = Modifier.padding(bottom = 8.dp).statusBarsPadding() // Thêm chút margin đáy
    ) {
        items(dates) { date ->
            val isSelected = date == selected
            val dateInfo = getFormattedDateInfo(date) // 🔥 Gọi hàm lấy thông tin ngày

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = !isLoading) { onClick(date) },
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                // Đổi thành Column để chứa 2 dòng
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Căn giữa chữ
                ) {
                    // Dòng 1: Thứ / Hôm nay
                    Text(
                        text = dateInfo.first,
                        style = MaterialTheme.typography.labelMedium,
                        // Nếu đang được chọn thì chữ trắng hơi mờ, không thì màu xám
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Dòng 2: Ngày/Tháng (In đậm và to hơn)
                    Text(
                        text = dateInfo.second,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}