package com.example.myapplication.domain.repository

import com.example.myapplication.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun getMessages(limit: Int, offset: Int): List<ChatMessageEntity>

    suspend fun sendMessage( message: String)

    suspend fun clearChat()

}