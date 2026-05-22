package com.example.myapplication.presentation.screen.admin.news.edit


import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.*
import com.example.myapplication.domain.repository.AdminNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import java.time.format.DateTimeFormatter

data class AdminNewsEditState(
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Lỗi chung cho Toast

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

    val existingThumbnailUrl: String? = null,
    val selectedImageUri: Uri? = null,

    val availableVouchers: List<AdminVoucherSimpleDto> = emptyList()
)

sealed class NewsEditEvent {
    data class TitleChanged(val v: String) : NewsEditEvent()
    data class ContentChanged(val v: String) : NewsEditEvent()
    data class PublishedChanged(val v: Boolean) : NewsEditEvent()
    data class TypeChanged(val v: String) : NewsEditEvent()
    data class StartDateChanged(val v: String) : NewsEditEvent()
    data class EndDateChanged(val v: String) : NewsEditEvent()
    data class VoucherSelected(val id: String?) : NewsEditEvent()
    data class ImageSelected(val uri: Uri?) : NewsEditEvent()
    data class SaveClicked(val context: Context) : NewsEditEvent()
}

@HiltViewModel
class AdminNewsEditViewModel @Inject constructor(
    private val repository: AdminNewsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val newsId: String = checkNotNull(savedStateHandle["newsId"])
    private val _state = MutableStateFlow(AdminNewsEditState())
    val state = _state.asStateFlow()

    init { loadAllData() }

    private fun loadAllData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true, error = null) }
            val postResult = repository.getPostById(newsId)
            val voucherResult = repository.getActiveVouchers()

            if (postResult.isSuccess) {
                val p = postResult.getOrNull()!!

                // Parse Date thông minh (hỗ trợ cả chuẩn ISO hoặc chuẩn custom từ Backend)
                fun parseDate(d: String?): String {
                    if (d.isNullOrEmpty()) return ""
                    return try {
                        // Thử parse nếu chuẩn ISO
                        val formatterIn = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        val formatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        LocalDateTime.parse(d.take(19), formatterIn).format(formatterOut)
                    } catch (e: Exception) {
                        // Fallback logic cũ
                        val parts = d.split(" - ")
                        if (parts.size == 2) {
                            val dParts = parts[1].split("/")
                            "${dParts[2]}-${dParts[1]}-${dParts[0]} ${parts[0]}"
                        } else d
                    }
                }

                _state.update { it.copy(
                    isLoadingData = false,
                    title = p.title,
                    content = p.content ?: "",
                    published = p.published,
                    type = p.type,
                    existingThumbnailUrl = p.thumbnailUrl,
                    startDateStr = parseDate(p.startDate),
                    endDateStr = parseDate(p.endDate),
                    availableVouchers = voucherResult.getOrDefault(emptyList()),
                    selectedVoucherId = p.id // Lấy ID voucher đã đính kèm nếu có
                ) }
            } else {
                _state.update { it.copy(isLoadingData = false, error = postResult.exceptionOrNull()?.message) }
            }
        }
    }

    fun onEvent(event: NewsEditEvent) {
        when (event) {
            // 🔥 Tự động xóa lỗi viền đỏ khi gõ lại
            is NewsEditEvent.TitleChanged -> _state.update { it.copy(title = event.v, titleError = null) }
            is NewsEditEvent.StartDateChanged -> _state.update { it.copy(startDateStr = event.v, startDateError = null) }
            is NewsEditEvent.EndDateChanged -> _state.update { it.copy(endDateStr = event.v, endDateError = null) }

            is NewsEditEvent.ContentChanged -> _state.update { it.copy(content = event.v) }
            is NewsEditEvent.PublishedChanged -> _state.update { it.copy(published = event.v) }
            is NewsEditEvent.TypeChanged -> _state.update { it.copy(type = event.v, selectedVoucherId = if (event.v == "NORMAL") null else it.selectedVoucherId) }
            is NewsEditEvent.VoucherSelected -> _state.update { it.copy(selectedVoucherId = event.id) }
            is NewsEditEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }
            is NewsEditEvent.SaveClicked -> savePost(event.context)
        }
    }

    // Dọn dẹp lỗi Toast
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun savePost(context: Context) {
        val st = _state.value
        var isValid = true

        if (st.title.isBlank()) {
            _state.update { it.copy(titleError = "Tiêu đề không được để trống") }
            isValid = false
        }

        if (st.type == "VOUCHER" && st.selectedVoucherId == null) {
            _state.update { it.copy(error = "Vui lòng chọn Voucher đính kèm bài viết") }
            isValid = false
        }

        if (!isValid) return

        // Format ISO Date
        var isoStart: String? = null
        var isoEnd: String? = null
        try {
            val formatterIn = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            if (st.startDateStr.isNotBlank()) isoStart = LocalDateTime.parse(st.startDateStr, formatterIn).toString()
            if (st.endDateStr.isNotBlank()) isoEnd = LocalDateTime.parse(st.endDateStr, formatterIn).toString()
        } catch (e: Exception) {
            _state.update { it.copy(error = "Định dạng thời gian bị lỗi!") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminPostUpdateRequest(
                title = st.title.trim(),
                content = st.content.trim(),
                published = st.published,
                type = st.type,
                startDate = isoStart,
                endDate = isoEnd,
                voucherId = st.selectedVoucherId
            )

            val result = repository.updatePost(newsId, request, st.selectedImageUri, context)
            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Cập nhật thất bại") }
            }
        }
    }
}