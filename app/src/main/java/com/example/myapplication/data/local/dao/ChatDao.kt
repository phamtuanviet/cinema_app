package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // Lưu 1 tin nhắn mới vào DB
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    // Lấy toàn bộ lịch sử chat của 1 session, sắp xếp theo thời gian cũ -> mới
    // Dùng Flow để UI tự động lắng nghe sự thay đổi
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesPaged(sessionId: String, limit: Int, offset: Int): List<ChatMessageEntity>

    // Tùy chọn: Xóa lịch sử chat của 1 session
    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun clearSession(sessionId: String)
}