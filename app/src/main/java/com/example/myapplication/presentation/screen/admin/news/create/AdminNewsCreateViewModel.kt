package com.example.myapplication.presentation.screen.admin.news.create


import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.*
import com.example.myapplication.domain.repository.AdminNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminNewsCreateState(
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Dùng cho Toast chung

    val title: String = "",
    val titleError: String? = null,

    val content: String = "",

    val published: Boolean = true,
    val type: String = "NORMAL",

    val startDateStr: String = "",
    val startDateError: String? = null,

    val endDateStr: String = "",
    val endDateError: String? = null,

    val selectedVoucherId: String? = null,
    val selectedImageUri: Uri? = null,
    val sendNotification: Boolean = false,

    val availableVouchers: List<AdminVoucherSimpleDto> = emptyList()
)

sealed class NewsCreateEvent {
    data class SendNotificationChanged(val v: Boolean) : NewsCreateEvent()
    data class TitleChanged(val v: String) : NewsCreateEvent()
    data class ContentChanged(val v: String) : NewsCreateEvent()
    data class TypeChanged(val v: String) : NewsCreateEvent()
    data class StartDateChanged(val v: String) : NewsCreateEvent()
    data class EndDateChanged(val v: String) : NewsCreateEvent()
    data class VoucherSelected(val id: String?) : NewsCreateEvent()
    data class ImageSelected(val uri: Uri?) : NewsCreateEvent()
    data class PublishedChanged(val v: Boolean) : NewsCreateEvent()
    data class SaveClicked(val context: Context) : NewsCreateEvent()
}

@HiltViewModel
class AdminNewsCreateViewModel @Inject constructor(
    private val repository: AdminNewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminNewsCreateState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val result = repository.getActiveVouchers()
            if (result.isSuccess) _state.update { it.copy(availableVouchers = result.getOrDefault(emptyList())) }
        }
    }

    fun onEvent(event: NewsCreateEvent) {
        when (event) {
            // 🔥 Xóa lỗi viền đỏ khi Admin gõ lại
            is NewsCreateEvent.TitleChanged -> _state.update { it.copy(title = event.v, titleError = null) }
            is NewsCreateEvent.StartDateChanged -> _state.update { it.copy(startDateStr = event.v, startDateError = null) }
            is NewsCreateEvent.EndDateChanged -> _state.update { it.copy(endDateStr = event.v, endDateError = null) }

            is NewsCreateEvent.ContentChanged -> _state.update { it.copy(content = event.v) }
            is NewsCreateEvent.TypeChanged -> _state.update { it.copy(type = event.v, selectedVoucherId = null) }
            is NewsCreateEvent.VoucherSelected -> _state.update { it.copy(selectedVoucherId = event.id) }
            is NewsCreateEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }
            is NewsCreateEvent.PublishedChanged -> _state.update { it.copy(published = event.v) }
            is NewsCreateEvent.SendNotificationChanged -> _state.update { it.copy(sendNotification = event.v) }
            is NewsCreateEvent.SaveClicked -> savePost(event.context)
        }
    }

    // Dọn dẹp lỗi Toast
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun savePost(context: Context) {
        val st = _state.value
        var isValid = true

        // 1. Kiểm tra ảnh bìa
        if (st.selectedImageUri == null) {
            _state.update { it.copy(error = "Vui lòng chọn ảnh bìa cho bài viết") }
            return
        }

        // 2. Kiểm tra tiêu đề
        if (st.title.isBlank()) {
            _state.update { it.copy(titleError = "Tiêu đề không được để trống") }
            isValid = false
        }

        // 3. Nếu là loại Voucher thì bắt buộc phải chọn Voucher
        if (st.type == "VOUCHER" && st.selectedVoucherId == null) {
            _state.update { it.copy(error = "Vui lòng chọn Voucher đính kèm bài viết") }
            isValid = false
        }

        // Dừng lại nếu form bị lỗi
        if (!isValid) return

        // Format thời gian từ Picker (yyyy-MM-dd HH:mm) sang ISO 8601 (yyyy-MM-ddTHH:mm:00)
        fun formatISO(s: String) = if(s.isNotBlank()) "${s.replace(" ", "T")}:00" else null

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminPostCreateRequest(
                title = st.title.trim(),
                content = st.content.trim(),
                published = st.published,
                type = st.type,
                startDate = formatISO(st.startDateStr),
                endDate = formatISO(st.endDateStr),
                voucherId = st.selectedVoucherId,
                sendNotification = st.sendNotification
            )

            val result = repository.createPost(request, st.selectedImageUri, context)
            if (result.isSuccess) _state.update { it.copy(isSaving = false, isSuccess = true) }
            else _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Phát hành thất bại") }
        }
    }
}