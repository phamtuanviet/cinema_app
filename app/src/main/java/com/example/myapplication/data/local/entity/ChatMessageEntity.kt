package com.example.myapplication.data.local.entity // Thay bằng package của bạn

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val sessionId: String, // ID của phiên chat (VD: "session_123")

    val role: String, // Phân biệt người gửi: "USER" hoặc "BOT"

    val content: String, // Nội dung chat (chữ)

    val timestamp: Long, // Thời gian gửi (System.currentTimeMillis())

    // ĐÂY LÀ CHÌA KHÓA: Lưu toàn bộ List<RawUiAction> thành 1 chuỗi JSON
    val rawUiActions: String?
)