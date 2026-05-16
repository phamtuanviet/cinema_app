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
import javax.inject.Inject

data class AdminNewsEditState(
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    val title: String = "",
    val content: String = "",
    val published: Boolean = true,
    val type: String = "NORMAL",
    val startDateStr: String = "", // yyyy-MM-dd HH:mm
    val endDateStr: String = "",
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
            _state.update { it.copy(isLoadingData = true) }
            val postResult = repository.getPostById(newsId)
            val voucherResult = repository.getActiveVouchers()

            if (postResult.isSuccess) {
                val p = postResult.getOrNull()!!
                // Parse date tương tự phần Voucher
                fun parseDate(d: String?) = d?.let {
                    val parts = it.split(" - ")
                    if (parts.size == 2) {
                        val dParts = parts[1].split("/")
                        "${dParts[2]}-${dParts[1]}-${dParts[0]} ${parts[0]}"
                    } else ""
                } ?: ""

                _state.update { it.copy(
                    isLoadingData = false,
                    title = p.title,
                    content = p.content ?: "",
                    published = p.published,
                    type = p.type,
                    existingThumbnailUrl = p.thumbnailUrl,
                    startDateStr = parseDate(p.startDate),
                    endDateStr = parseDate(p.endDate),
                    availableVouchers = voucherResult.getOrDefault(emptyList())
                    // selectedVoucherId cần backend trả về ID voucher trong DTO list, nếu chưa có hãy bổ sung
                ) }
            } else {
                _state.update { it.copy(isLoadingData = false, error = postResult.exceptionOrNull()?.message) }
            }
        }
    }

    fun onEvent(event: NewsEditEvent) {
        when (event) {
            is NewsEditEvent.TitleChanged -> _state.update { it.copy(title = event.v) }
            is NewsEditEvent.ContentChanged -> _state.update { it.copy(content = event.v) }
            is NewsEditEvent.PublishedChanged -> _state.update { it.copy(published = event.v) }
            is NewsEditEvent.TypeChanged -> _state.update { it.copy(type = event.v, selectedVoucherId = if(event.v == "NORMAL") null else it.selectedVoucherId) }
            is NewsEditEvent.StartDateChanged -> _state.update { it.copy(startDateStr = event.v) }
            is NewsEditEvent.EndDateChanged -> _state.update { it.copy(endDateStr = event.v) }
            is NewsEditEvent.VoucherSelected -> _state.update { it.copy(selectedVoucherId = event.id) }
            is NewsEditEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }
            is NewsEditEvent.SaveClicked -> savePost(event.context)
        }
    }

    private fun savePost(context: Context) {
        val st = _state.value
        if (st.title.isBlank()) { _state.update { it.copy(error = "Tiêu đề không được trống") }; return }

        fun formatISO(s: String) = if(s.isNotBlank()) "${s.replace(" ", "T")}:00" else null

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val request = AdminPostUpdateRequest(
                title = st.title, content = st.content, published = st.published,
                type = st.type, startDate = formatISO(st.startDateStr),
                endDate = formatISO(st.endDateStr), voucherId = st.selectedVoucherId
            )
            val result = repository.updatePost(newsId, request, st.selectedImageUri, context)
            if (result.isSuccess) _state.update { it.copy(isSaving = false, isSuccess = true) }
            else _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
        }
    }
}