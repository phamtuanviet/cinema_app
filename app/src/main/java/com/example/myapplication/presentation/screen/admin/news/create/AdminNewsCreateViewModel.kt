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
    val error: String? = null,

    val title: String = "",
    val content: String = "",
    val published: Boolean = true,
    val type: String = "NORMAL",
    val startDateStr: String = "",
    val endDateStr: String = "",
    val selectedVoucherId: String? = null,
    val selectedImageUri: Uri? = null,

    val availableVouchers: List<AdminVoucherSimpleDto> = emptyList()
)

sealed class NewsCreateEvent {
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
            is NewsCreateEvent.TitleChanged -> _state.update { it.copy(title = event.v) }
            is NewsCreateEvent.ContentChanged -> _state.update { it.copy(content = event.v) }
            is NewsCreateEvent.TypeChanged -> _state.update { it.copy(type = event.v, selectedVoucherId = null) }
            is NewsCreateEvent.StartDateChanged -> _state.update { it.copy(startDateStr = event.v) }
            is NewsCreateEvent.EndDateChanged -> _state.update { it.copy(endDateStr = event.v) }
            is NewsCreateEvent.VoucherSelected -> _state.update { it.copy(selectedVoucherId = event.id) }
            is NewsCreateEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }
            is NewsCreateEvent.PublishedChanged -> _state.update { it.copy(published = event.v) }
            is NewsCreateEvent.SaveClicked -> savePost(event.context)
        }
    }

    private fun savePost(context: Context) {
        val st = _state.value
        if (st.title.isBlank()) { _state.update { it.copy(error = "Vui lòng nhập tiêu đề bài viết") }; return }

        fun formatISO(s: String) = if(s.isNotBlank()) "${s.replace(" ", "T")}:00" else null

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val request = AdminPostCreateRequest(
                title = st.title, content = st.content, published = st.published,
                type = st.type, startDate = formatISO(st.startDateStr),
                endDate = formatISO(st.endDateStr), voucherId = st.selectedVoucherId
            )
            val result = repository.createPost(request, st.selectedImageUri, context)
            if (result.isSuccess) _state.update { it.copy(isSaving = false, isSuccess = true) }
            else _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
        }
    }
}