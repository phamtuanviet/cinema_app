package com.example.myapplication.presentation.screen.chatbot

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entity.ChatMessageEntity
import com.example.myapplication.data.remote.dto.RawUiAction
import com.example.myapplication.data.remote.dto.UserLocation
import com.example.myapplication.domain.model.ChatMessageUi
import com.example.myapplication.domain.repository.ChatRepository
import com.example.myapplication.util.ChatActionMapper
import com.google.android.gms.location.FusedLocationProviderClient
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
    private val gson: Gson,
    private val fusedLocationClient: FusedLocationProviderClient
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

    private fun loadInitialMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }

            val entities = repository.getMessages(limit = pageSize, offset = 0)
            val uiMessages = entities.map { mapEntityToUi(it) }.reversed()

            currentOffset = pageSize

            _uiState.update {
                it.copy(
                    messages = uiMessages,
                    isLoadingHistory = false,
                    isLastPage = entities.size < pageSize
                )
            }
        }
    }

    fun fetchLocationAndSend() {
        _uiState.update { it.copy(isSending = true, errorMessage = null) }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val userLoc = UserLocation(
                            lat = location.latitude,
                            lng = location.longitude
                        )
                        sendInternalMessage(text = "Đã chia sẻ vị trí", location = userLoc)
                    } else {
                        _uiState.update {
                            it.copy(isSending = false, errorMessage = "Không tìm thấy vị trí. Hãy bật GPS.")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    _uiState.update {
                        it.copy(isSending = false, errorMessage = "Lỗi lấy vị trí: ${e.message}")
                    }
                }
        } catch (e: SecurityException) {
            _uiState.update { it.copy(isSending = false, errorMessage = "Thiếu quyền truy cập vị trí.") }
        }
    }

    // ĐÃ SỬA: Cập nhật UI ngay lập tức trước khi gọi Repository
    private fun sendInternalMessage(text: String, location: UserLocation?) {
        val currentSize = _uiState.value.messages.size

        // 1. Tạo tin nhắn tạm (ảo) cho User để hiển thị ngay lập tức
        val tempUserMessage = ChatMessageUi(
            id = System.currentTimeMillis(), // ID tạm
            sessionId = "current_session",
            role = "USER",// Đảm bảo role này đúng với logic check "isFromUser" trong UI của bạn
            content = text,
            timestamp = System.currentTimeMillis(),
            actions = emptyList()
        )

        // 2. Cập nhật state hiển thị ngay lên màn hình
        _uiState.update {
            it.copy(
                messages = it.messages + tempUserMessage,
                isSending = true
            )
        }

        viewModelScope.launch {
            try {
                // Gọi repository với tham số location
                repository.sendMessage(text, location)

                // Refresh danh sách tin nhắn để lấy tin chuẩn từ DB (gồm cả tin user đã lưu và tin bot)
                val newLimit = currentSize + 2
                val updatedEntities = repository.getMessages(limit = newLimit, offset = 0)

                _uiState.update {
                    it.copy(
                        messages = updatedEntities.map { mapEntityToUi(it) }.reversed(),
                        isSending = false
                    )
                }
                currentOffset = newLimit
            } catch (e: Exception) {
                // Nếu lỗi, rollback UI bằng cách xóa tin nhắn tạm và báo lỗi
                _uiState.update {
                    it.copy(
                        messages = it.messages.filter { msg -> msg.id != tempUserMessage.id },
                        isSending = false,
                        errorMessage = "Gửi vị trí thất bại."
                    )
                }
            }
        }
    }

    fun loadMoreMessages() {
        if (_uiState.value.isLoadingHistory || _uiState.value.isLastPage) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }

            val oldEntities = repository.getMessages(limit = pageSize, offset = currentOffset)

            if (oldEntities.isNotEmpty()) {
                val oldUiMsgs = oldEntities.map { mapEntityToUi(it) }.reversed()
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

    // ĐÃ SỬA: Cập nhật UI ngay lập tức trước khi gọi Repository
    fun sendMessage(text: String) {
        if (text.isBlank() || _uiState.value.isSending) return

        val currentSize = _uiState.value.messages.size

        // 1. Tạo tin nhắn tạm (ảo) cho User
        val tempUserMessage = ChatMessageUi(
            id = System.currentTimeMillis(), // Sinh ID tạm bằng timestamp
            sessionId = "current_session",
            role = "USER", // Hoặc "me" tùy thuộc vào logic phân biệt role trong ChatMessageUi của bạn
            content = text,
            timestamp = System.currentTimeMillis(),
            actions = emptyList()
        )

        // 2. Cập nhật UI ngay lập tức, bật trạng thái isSending = true
        _uiState.update {
            it.copy(
                messages = it.messages + tempUserMessage,
                isSending = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                // 3. Tiến hành gọi ngầm
                repository.sendMessage(text.trim(), null)

                // 4. Lấy dữ liệu chuẩn từ DB lên (gồm tin của user thật trong DB + tin của Bot)
                val newLimit = currentSize + 2
                val updatedEntities = repository.getMessages(limit = newLimit, offset = 0)

                _uiState.update {
                    it.copy(
                        messages = updatedEntities.map { entity -> mapEntityToUi(entity) }.reversed(),
                        isSending = false
                    )
                }

                currentOffset = newLimit
            } catch (e: Exception) {
                // Bắt lỗi, khôi phục giao diện (bỏ tin nhắn lỗi ra khỏi màn hình)
                _uiState.update {
                    it.copy(
                        messages = it.messages.filter { msg -> msg.id != tempUserMessage.id },
                        isSending = false,
                        errorMessage = "Không thể gửi tin nhắn. Hãy kiểm tra kết nối."
                    )
                }
            }
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }

            repository.clearChat()

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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // =========================================================================
    // HÀM BỔ TRỢ (HELPER)
    // =========================================================================

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