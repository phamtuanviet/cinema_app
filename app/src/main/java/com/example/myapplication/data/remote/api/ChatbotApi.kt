package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.ChatRequest
import com.example.myapplication.data.remote.dto.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatbotApi {
    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): Response<ChatResponse>

    @DELETE("chat/session/{sessionId}")
    suspend fun clearChatSession(@Path("sessionId") sessionId: String): Response<Unit>
}