package com.example.myapplication.presentation.component



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Schedule

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.PostDetailResponse
import com.example.myapplication.data.remote.enums.PostType

@Composable
fun PromotionContent(
    post: PostDetailResponse,
    onNavigateToVoucher: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        // 1. Ảnh Cover full cạnh
        if (!post.thumbnailUrl.isNullOrEmpty()) {
            AsyncImage(
                model = post.thumbnailUrl,
                contentDescription = "Cover Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // 2. Tiêu đề
            Text(
                text = post.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Tag (Chip) hiển thị loại và thời gian (Chuẩn Material)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (post.type == PostType.VOUCHER) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Voucher") },
                        leadingIcon = {
                            Icon(Icons.Rounded.LocalOffer, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            leadingIconContentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (!post.endDate.isNullOrEmpty()) {
                    AssistChip(
                        onClick = { },
                        label = { Text("HSD: ${post.endDate}") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Component Voucher Card nâng cao
            if (post.type == PostType.VOUCHER && post.voucher != null) {
                VoucherCard(
                    voucher = post.voucher,
                    onNavigateToVoucher = onNavigateToVoucher
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // 5. Nội dung chi tiết
            Text(
                text = "Thông tin chương trình",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }
    }
}