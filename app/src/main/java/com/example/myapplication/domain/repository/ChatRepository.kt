package com.example.myapplication.domain.repository

import com.example.myapplication.data.local.entity.ChatMessageEntity
import com.example.myapplication.data.remote.dto.UserLocation
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun getMessages(limit: Int, offset: Int): List<ChatMessageEntity>

    suspend fun sendMessage( message: String,location: UserLocation?)

    suspend fun clearChat()

}