package com.example.myapplication.data.remote.repository

import android.util.Log
import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.local.dao.ChatDao
import com.example.myapplication.data.local.entity.ChatMessageEntity
import com.example.myapplication.data.remote.api.ChatbotApi
import com.example.myapplication.data.remote.dto.ChatRequest
import com.example.myapplication.data.remote.dto.UserLocation
import com.example.myapplication.domain.repository.ChatRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatbotApi: ChatbotApi,
    private val chatDao: ChatDao,
    private val gson: Gson,
    private val sessionManager: SessionManager
) : ChatRepository {

    suspend fun getSessionId(): String {
        val user = sessionManager.getUser()

        val userId = user?.id ?: "guest_user"

        return "session_$userId"
    }

    override suspend fun getMessages(limit: Int, offset: Int): List<ChatMessageEntity> {
        return chatDao.getMessagesPaged(getSessionId(), limit, offset)
    }
    // 2. Gửi tin nhắn mới
    override suspend fun sendMessage(message: String,location: UserLocation?) {
        val sessionId = getSessionId() // TỰ ĐỘNG lấy sessionId tại đây!
        val currentTime = System.currentTimeMillis()

        // Bước A: Lưu tin nhắn của User vào DB ngay lập tức để UI hiển thị luôn
        val userMessage = ChatMessageEntity(
            sessionId = sessionId,
            role = "USER",
            content = message,
            timestamp = currentTime,
            rawUiActions = null // User thì không có UI Action
        )
        chatDao.insertMessage(userMessage)

        Log.d("ChatRepository", "Sending message: $userMessage")

        try {
            // Bước B: Gọi API Node.js
            // Ghi chú: Nếu bạn có lấy tọa độ GPS thực tế thì truyền vào đây, tạm thời mình để null
            val request = ChatRequest(message = message, chatSessionId = sessionId, location = location)
            val response = chatbotApi.sendMessage(request)

            if (response.isSuccessful && response.body() != null) {
                val chatData = response.body()!!

                // Bước C: Chuyển mảng uiActions thành String JSON để lưu DB
                val rawActionsString = if (!chatData.uiActions.isNullOrEmpty()) {
                    gson.toJson(chatData.uiActions)
                } else {
                    null
                }

                // Bước D: Lưu tin trả lời của Bot vào DB
                val botMessage = ChatMessageEntity(
                    sessionId = sessionId,
                    role = "BOT",
                    content = chatData.content,
                    timestamp = System.currentTimeMillis(),
                    rawUiActions = rawActionsString
                )
                chatDao.insertMessage(botMessage)
            } else {
                saveErrorMessage(sessionId, "Hệ thống chatbot đang bận. Vui lòng thử lại sau.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ChatRepository", "Error sending message: ${e.message}")
            saveErrorMessage(sessionId, "Mất kết nối đến máy chủ. Vui lòng kiểm tra mạng.")
        }
    }

    private suspend fun saveErrorMessage(sessionId: String, errorText: String) {
        val errorMsg = ChatMessageEntity(
            sessionId = sessionId,
            role = "BOT",
            content = errorText,
            timestamp = System.currentTimeMillis(),
            rawUiActions = null
        )
        chatDao.insertMessage(errorMsg)
    }

    override suspend fun clearChat() {
        val sessionId = getSessionId()
        try {
            // 1. Gọi API xóa trên server trước
            val response = chatbotApi.clearChatSession(sessionId)
            if (response.isSuccessful) {
                // 2. Nếu server xóa xong, xóa trắng Room DB
                chatDao.clearSession(sessionId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Tùy chọn: Vẫn xóa DB cục bộ nếu bạn muốn "New Chat" ngay lập tức
            chatDao.clearSession(sessionId)
        }
    }
}