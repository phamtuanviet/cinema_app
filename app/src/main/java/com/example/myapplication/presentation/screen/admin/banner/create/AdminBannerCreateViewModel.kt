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
    val error: String? = null,

    val actionType: String = "MOVIE",
    val targetUrl: String = "",
    val selectedMovieId: String? = null,
    val priorityStr: String = "0",
    val isActive: Boolean = true,

    val selectedImageUri: Uri? = null, // Bắt buộc phải có khi Save

    val availableMovies: List<AdminMovieSimpleDto> = emptyList()
)

sealed class BannerCreateEvent {
    data class ActionTypeChanged(val type: String) : BannerCreateEvent()
    data class TargetUrlChanged(val url: String) : BannerCreateEvent()
    data class MovieSelected(val id: String?) : BannerCreateEvent()
    data class PriorityChanged(val priority: String) : BannerCreateEvent()
    data class IsActiveChanged(val isActive: Boolean) : BannerCreateEvent()
    data class ImageSelected(val uri: Uri?) : BannerCreateEvent()
    data class SaveClicked(val context: Context) : BannerCreateEvent()
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
            is BannerCreateEvent.ActionTypeChanged -> _state.update {
                it.copy(actionType = event.type, selectedMovieId = null, targetUrl = "")
            }
            is BannerCreateEvent.TargetUrlChanged -> _state.update { it.copy(targetUrl = event.url) }
            is BannerCreateEvent.MovieSelected -> _state.update { it.copy(selectedMovieId = event.id) }
            is BannerCreateEvent.PriorityChanged -> _state.update { it.copy(priorityStr = event.priority) }
            is BannerCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is BannerCreateEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }
            is BannerCreateEvent.SaveClicked -> saveBanner(event.context)
        }
    }

    private fun saveBanner(context: Context) {
        val st = _state.value

        // Validate
        if (st.selectedImageUri == null) {
            _state.update { it.copy(error = "Vui lòng chọn ảnh Banner") }; return
        }
        if (st.actionType == "URL" && st.targetUrl.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập đường dẫn URL") }; return
        }
        if (st.actionType == "MOVIE" && st.selectedMovieId == null) {
            _state.update { it.copy(error = "Vui lòng chọn Phim liên kết") }; return
        }

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
            else _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
        }
    }
}