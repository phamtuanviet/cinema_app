package com.example.myapplication.presentation.screen.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entity.ChatMessageEntity
import com.example.myapplication.data.remote.dto.RawUiAction
import com.example.myapplication.domain.model.ChatMessageUi
import com.example.myapplication.domain.repository.ChatRepository
import com.example.myapplication.util.ChatActionMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val mapper: ChatActionMapper,
    private val gson: Gson
) : ViewModel() {

    // --- STATE ---
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // --- BIẾN PHÂN TRANG ---
    private var currentOffset = 0
    private val pageSize = 20

    init {
        loadInitialMessages()
    }

    // =========================================================================
    // CÁC HÀM TƯƠNG TÁC TỪ UI (INTENTS)
    // =========================================================================

    // 1. Nạp dữ liệu lần đầu
    private fun loadInitialMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }

            val entities = repository.getMessages(limit = pageSize, offset = 0)

            // Vì Room trả về tin nhắn mới nhất trước (DESC), ta phải .reversed() lại để tin cũ nằm trên, tin mới nằm dưới cùng
            val uiMessages = entities.map { mapEntityToUi(it) }.reversed()

            currentOffset = pageSize

            _uiState.update {
                it.copy(
                    messages = uiMessages,
                    isLoadingHistory = false,
                    isLastPage = entities.size < pageSize // Nếu số lượng lấy được < 20 -> Đã hết sạch lịch sử
                )
            }
        }
    }

    // 2. Kéo lên để tải thêm lịch sử (Load More)
    fun loadMoreMessages() {
        // Đang tải dở hoặc đã hết lịch sử thì không tải nữa
        if (_uiState.value.isLoadingHistory || _uiState.value.isLastPage) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }

            val oldEntities = repository.getMessages(limit = pageSize, offset = currentOffset)

            if (oldEntities.isNotEmpty()) {
                val oldUiMsgs = oldEntities.map { mapEntityToUi(it) }.reversed()

                // Nối tin nhắn cũ lên ĐẦU danh sách hiện tại
                val updatedMessages = oldUiMsgs + _uiState.value.messages
                currentOffset += pageSize

                _uiState.update {
                    it.copy(
                        messages = updatedMessages,
                        isLoadingHistory = false,
                        isLastPage = oldEntities.size < pageSize
                    )
                }
            } else {
                _uiState.update { it.copy(isLoadingHistory = false, isLastPage = true) }
            }
        }
    }

    // 3. Gửi tin nhắn mới
    fun sendMessage(text: String) {
        if (text.isBlank() || _uiState.value.isSending) return

        viewModelScope.launch {
            // Hiển thị trạng thái "Đang gửi"
            _uiState.update { it.copy(isSending = true, errorMessage = null) }

            // Gọi Repo để lưu vào DB -> Gọi Node.js -> Lưu kết quả Node.js vào DB
            try {
                repository.sendMessage(text.trim())
            } catch (e: Exception) {
                // Nếu lỗi kết nối nặng (bắt thêm ở đây cho chắc)
                _uiState.update { it.copy(errorMessage = "Không thể gửi tin nhắn. Hãy kiểm tra kết nối.") }
            }

            // Gửi xong, chúng ta làm mới lại danh sách (Lấy thêm 2 tin nhắn vừa sinh ra: 1 của user, 1 của bot)
            // Để đơn giản và đồng bộ, ta nạp lại offset = 0 với số lượng tin nhắn hiện tại + 2
            val newLimit = _uiState.value.messages.size + 2
            val updatedEntities = repository.getMessages(limit = newLimit, offset = 0)

            _uiState.update {
                it.copy(
                    messages = updatedEntities.map { mapEntityToUi(it) }.reversed(),
                    isSending = false
                )
            }

            // Cập nhật lại Offset để Load More không bị lệch
            currentOffset = newLimit
        }
    }

    // 4. Dọn dẹp phiên chat (New Chat)
    fun clearSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }

            // Gọi Node.js xóa Redis và xóa Room DB
            repository.clearChat()

            // Đưa State về trạng thái trắng tinh
            currentOffset = 0
            _uiState.update {
                it.copy(
                    messages = emptyList(),
                    isLoadingHistory = false,
                    isLastPage = true,
                    isSending = false
                )
            }
        }
    }

    // 5. Tắt thông báo lỗi (được gọi từ UI sau khi Toast/Snackbar đã hiện xong)
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // =========================================================================
    // HÀM BỔ TRỢ (HELPER)
    // =========================================================================

    // Biến dữ liệu thô từ Room DB thành dữ liệu sạch sẽ cho UI vẽ
    private fun mapEntityToUi(entity: ChatMessageEntity): ChatMessageUi {
        val rawActions: List<RawUiAction> = if (!entity.rawUiActions.isNullOrEmpty()) {
            val type = object : TypeToken<List<RawUiAction>>() {}.type
            try {
                gson.fromJson(entity.rawUiActions, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        // Dùng Mapper ở Bước 4 để ép kiểu sang các Sealed Class
        val mappedActions = rawActions.map { mapper.map(it) }

        return ChatMessageUi(
            id = entity.id,
            sessionId = entity.sessionId,
            role = entity.role,
            content = entity.content,
            timestamp = entity.timestamp,
            actions = mappedActions
        )
    }
}