package com.example.myapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.jeziellago.compose.markdowntext.MarkdownText

import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.ChatMessageUi

@Composable
fun ChatMessageBubble(
    message: ChatMessageUi,
    onMovieClick: (String) -> Unit,
    onBookTicketClick: (String) -> Unit
)  {
    val isUser = message.role == "USER"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Bong bóng chứa Text
        Box(
            modifier = Modifier
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            MarkdownText(
                markdown = message.content,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge, // Dùng font chữ chuẩn của app
                disableLinkMovementMethod = true // Tắt click link mặc định để tránh lỗi điều hướng nếu không cần thiết
            )
        }

        // Vẽ các UI Action (Danh sách phim, giờ chiếu...) nếu có
        if (message.role == "BOT" && message.actions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            message.actions.forEach { action ->
                ChatActionRenderer(
                    action = action,
                    onMovieClick = onMovieClick,           // Truyền tiếp xuống
                    onBookTicketClick = onBookTicketClick  // Truyền tiếp xuống
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}