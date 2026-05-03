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
import androidx.navigation.NavController
import com.example.myapplication.domain.model.ChatMessageUi

@Composable
fun ChatMessageBubble(
    message: ChatMessageUi,
    navController: NavController,       // Thay đổi: Nhận trực tiếp NavController
    onLocationRequest: () -> Unit       // Thay đổi: Nhận callback lấy vị trí
) {
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
                style = MaterialTheme.typography.bodyLarge,
                disableLinkMovementMethod = true
            )
        }

        // Vẽ các UI Action
        if (!isUser && message.actions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            message.actions.forEach { action ->
                ChatActionRenderer(
                    action = action,
                    navController = navController,      // Truyền đúng tham số
                    onLocationRequest = onLocationRequest // Truyền đúng tham số
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}