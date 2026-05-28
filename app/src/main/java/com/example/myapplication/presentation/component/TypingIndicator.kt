package com.example.myapplication.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tạo độ trễ (delay) cho từng dấu chấm để chúng nảy đuổi nhau
        val delays = listOf(0, 150, 300)

        delays.forEach { delay ->
            val offset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 900
                        0f at delay                 // Bắt đầu
                        -8f at delay + 300          // Nảy lên 8dp
                        0f at delay + 600           // Rơi xuống
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dot_bounce"
            )

            Box(
                modifier = Modifier
                    .offset(y = offset.dp) // Áp dụng hiệu ứng nảy theo trục Y
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}