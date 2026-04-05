package com.example.myapplication.presentation.screen.chatbot

import com.example.myapplication.domain.model.ChatMessageUi


data class ChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),

    val isSending: Boolean = false,

    val isLoadingHistory: Boolean = false,

    val isLastPage: Boolean = false,

    val errorMessage: String? = null
)