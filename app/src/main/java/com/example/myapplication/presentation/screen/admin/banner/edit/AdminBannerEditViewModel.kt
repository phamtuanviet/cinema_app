package com.example.myapplication.presentation.screen.admin.banner.edit

import com.example.myapplication.domain.repository.AdminBannerRepository
import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminBannerEditState(
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Dùng cho lỗi chung (Toast)

    val actionType: String = "MOVIE",
    val movieSearchQuery: String = "",
    val targetUrl: String = "",
    val targetUrlError: String? = null, // Lỗi ô URL

    val selectedMovieId: String? = null,
    val movieError: String? = null, // Lỗi ô chọn Phim

    val priorityStr: String = "0",
    val isActive: Boolean = true,

    val existingImageUrl: String? = null,
    val selectedImageUri: Uri? = null,

    val availableMovies: List<AdminMovieSimpleDto> = emptyList()
)
sealed class BannerEditEvent {
    data class ActionTypeChanged(val type: String) : BannerEditEvent()
    data class TargetUrlChanged(val url: String) : BannerEditEvent()
    data class MovieSelected(val id: String?, val title: String) : BannerEditEvent()
    data class MovieSearchQueryChanged(val query: String) : BannerEditEvent()
    data class PriorityChanged(val priority: String) : BannerEditEvent()
    data class IsActiveChanged(val isActive: Boolean) : BannerEditEvent()
    data class ImageSelected(val uri: Uri?) : BannerEditEvent()
    data class SaveClicked(val context: Context) : BannerEditEvent()
}

@HiltViewModel
class AdminBannerEditViewModel @Inject constructor(
    private val repository: AdminBannerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bannerId: String = checkNotNull(savedStateHandle["bannerId"])
    private val _state = MutableStateFlow(AdminBannerEditState())
    val state = _state.asStateFlow()

    init { loadAllData() }

    private fun loadAllData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true, error = null) }
            val bannerResult = repository.getBannerById(bannerId)
            val movieResult = repository.getActiveMovies()

            if (bannerResult.isSuccess) {
                val b = bannerResult.getOrNull()!!
                val movies = movieResult.getOrDefault(emptyList())
                val initialMovieTitle = movies.find { it.id == b.movieId }?.title ?: ""
                _state.update { it.copy(
                    isLoadingData = false,
                    actionType = b.actionType,
                    targetUrl = b.targetUrl ?: "",
                    selectedMovieId = b.movieId,
                    movieSearchQuery = initialMovieTitle,
                    priorityStr = b.priority.toString(),
                    isActive = b.isActive,
                    existingImageUrl = b.imageUrl,
                    availableMovies = movieResult.getOrDefault(emptyList())
                ) }
            } else {
                _state.update { it.copy(isLoadingData = false, error = bannerResult.exceptionOrNull()?.message ?: "Lỗi tải dữ liệu") }
            }
        }
    }

    fun onEvent(event: BannerEditEvent) {
        when (event) {
            // 🔥 Xóa lỗi viền đỏ khi người dùng thay đổi giá trị
            is BannerEditEvent.ActionTypeChanged -> _state.update {
                it.copy(
                    actionType = event.type,
                    selectedMovieId = if(event.type == "URL") null else it.selectedMovieId,
                    targetUrl = if(event.type == "MOVIE") "" else it.targetUrl,
                    targetUrlError = null,
                    movieError = null
                )
            }
            is BannerEditEvent.MovieSearchQueryChanged -> _state.update {
                it.copy(movieSearchQuery = event.query, selectedMovieId = null, movieError = null)
            }
            is BannerEditEvent.TargetUrlChanged -> _state.update { it.copy(targetUrl = event.url, targetUrlError = null) }
            is BannerEditEvent.MovieSelected -> _state.update {
                it.copy(selectedMovieId = event.id, movieSearchQuery = event.title, movieError = null)
            }
            is BannerEditEvent.PriorityChanged -> _state.update { it.copy(priorityStr = event.priority) }
            is BannerEditEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is BannerEditEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }
            is BannerEditEvent.SaveClicked -> saveBanner(event.context)
        }
    }

    // Hàm xóa lỗi Toast
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun saveBanner(context: Context) {
        val st = _state.value
        var isValid = true

        // Validate Inline
        if (st.actionType == "URL" && st.targetUrl.isBlank()) {
            _state.update { it.copy(targetUrlError = "Vui lòng nhập đường dẫn URL") }
            isValid = false
        }
        if (st.actionType == "MOVIE" && st.selectedMovieId == null) {
            _state.update { it.copy(movieError = "Vui lòng chọn Phim liên kết") }
            isValid = false
        }

        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val request = AdminBannerUpdateRequest(
                actionType = st.actionType,
                targetUrl = st.targetUrl.takeIf { it.isNotBlank() },
                movieId = st.selectedMovieId,
                priority = st.priorityStr.toIntOrNull() ?: 0,
                isActive = st.isActive
            )
            val result = repository.updateBanner(bannerId, request, st.selectedImageUri, context)
            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Cập nhật thất bại") }
            }
        }
    }
}