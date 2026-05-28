package com.example.myapplication.presentation.screen.admin.banner.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.*
import com.example.myapplication.domain.repository.AdminBannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AdminBannerCreateState(
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Lỗi chung cho Toast (VD: Lỗi mạng, Quên chọn ảnh)

    val actionType: String = "MOVIE", // "MOVIE" hoặc "URL"

    val targetUrl: String = "",
    val targetUrlError: String? = null,

    val selectedMovieId: String? = null,
    val movieError: String? = null,

    val priorityStr: String = "0",
    val isActive: Boolean = true,

    val selectedImageUri: Uri? = null,

    val availableMovies: List<AdminMovieSimpleDto> = emptyList(),
    val movieSearchQuery: String = "",
)

sealed class BannerCreateEvent {
    data class ActionTypeChanged(val type: String) : BannerCreateEvent()
    data class TargetUrlChanged(val url: String) : BannerCreateEvent()
    data class PriorityChanged(val priority: String) : BannerCreateEvent()
    data class IsActiveChanged(val isActive: Boolean) : BannerCreateEvent()
    data class ImageSelected(val uri: Uri?) : BannerCreateEvent()
    data class SaveClicked(val context: Context) : BannerCreateEvent()
    data class MovieSearchQueryChanged(val query: String) : BannerCreateEvent()
    data class MovieSelected(val id: String?, val title: String) : BannerCreateEvent()
}

@HiltViewModel
class AdminBannerCreateViewModel @Inject constructor(
    private val repository: AdminBannerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminBannerCreateState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val movieResult = repository.getActiveMovies()
            if (movieResult.isSuccess) {
                _state.update { it.copy(availableMovies = movieResult.getOrDefault(emptyList())) }
            }
        }
    }

    fun onEvent(event: BannerCreateEvent) {
        when (event) {
            //  Tự động dọn lỗi khi người dùng thay đổi lựa chọn
            is BannerCreateEvent.ActionTypeChanged -> _state.update {
                it.copy(
                    actionType = event.type,
                    selectedMovieId = null,
                    targetUrl = "",
                    targetUrlError = null,
                    movieError = null
                )
            }
            is BannerCreateEvent.TargetUrlChanged -> _state.update { it.copy(targetUrl = event.url, targetUrlError = null) }
            is BannerCreateEvent.MovieSearchQueryChanged -> _state.update {
                it.copy(movieSearchQuery = event.query, selectedMovieId = null, movieError = null)
            }
            is BannerCreateEvent.MovieSelected -> _state.update {
                it.copy(selectedMovieId = event.id, movieSearchQuery = event.title, movieError = null)
            }
            is BannerCreateEvent.PriorityChanged -> _state.update { it.copy(priorityStr = event.priority) }
            is BannerCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is BannerCreateEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri, error = null) }
            is BannerCreateEvent.SaveClicked -> saveBanner(event.context)
        }
    }

    // Dọn dẹp lỗi Toast
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun saveBanner(context: Context) {
        val st = _state.value
        var isValid = true

        // 1. Kiểm tra ảnh (Bắt buộc) - Hiển thị bằng Toast
        if (st.selectedImageUri == null) {
            _state.update { it.copy(error = "Vui lòng chọn ảnh bìa cho Banner") }
            return
        }

        // 2. Kiểm tra dữ liệu theo ActionType - Hiển thị bằng Viền đỏ
        if (st.actionType == "URL" && st.targetUrl.isBlank()) {
            _state.update { it.copy(targetUrlError = "Vui lòng nhập đường dẫn URL") }
            isValid = false
        }
        if (st.actionType == "MOVIE" && st.selectedMovieId == null) {
            _state.update { it.copy(movieError = "Vui lòng chọn Phim liên kết") }
            isValid = false
        }

        // Nếu form lỗi, dừng ngay
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val request = AdminBannerCreateRequest(
                actionType = st.actionType,
                targetUrl = st.targetUrl.takeIf { it.isNotBlank() },
                movieId = st.selectedMovieId,
                priority = st.priorityStr.toIntOrNull() ?: 0,
                isActive = st.isActive
            )
            val result = repository.createBanner(request, st.selectedImageUri, context)
            if (result.isSuccess) _state.update { it.copy(isSaving = false, isSuccess = true) }
            else _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Có lỗi xảy ra") }
        }
    }
}