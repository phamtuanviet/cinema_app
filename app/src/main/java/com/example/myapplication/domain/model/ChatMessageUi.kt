package com.example.myapplication.domain.model

data class ChatMessageUi(
    val id: Long,
    val sessionId: String,
    val role: String, // "USER" hoặc "BOT"
    val content: String,
    val timestamp: Long,
    val actions: List<ChatUiAction>
)