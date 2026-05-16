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
    val error: String? = null,

    val actionType: String = "MOVIE",
    val targetUrl: String = "",
    val selectedMovieId: String? = null,
    val priorityStr: String = "0",
    val isActive: Boolean = true,

    val existingImageUrl: String? = null,
    val selectedImageUri: Uri? = null,

    val availableMovies: List<AdminMovieSimpleDto> = emptyList()
)

sealed class BannerEditEvent {
    data class ActionTypeChanged(val type: String) : BannerEditEvent()
    data class TargetUrlChanged(val url: String) : BannerEditEvent()
    data class MovieSelected(val id: String?) : BannerEditEvent()
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
            _state.update { it.copy(isLoadingData = true) }
            val bannerResult = repository.getBannerById(bannerId)
            val movieResult = repository.getActiveMovies()

            if (bannerResult.isSuccess) {
                val b = bannerResult.getOrNull()!!
                _state.update { it.copy(
                    isLoadingData = false,
                    actionType = b.actionType,
                    targetUrl = b.targetUrl ?: "",
                    selectedMovieId = b.movieId,
                    priorityStr = b.priority.toString(),
                    isActive = b.isActive,
                    existingImageUrl = b.imageUrl,
                    availableMovies = movieResult.getOrDefault(emptyList())
                ) }
            } else {
                _state.update { it.copy(isLoadingData = false, error = bannerResult.exceptionOrNull()?.message) }
            }
        }
    }

    fun onEvent(event: BannerEditEvent) {
        when (event) {
            is BannerEditEvent.ActionTypeChanged -> _state.update {
                it.copy(actionType = event.type, selectedMovieId = if(event.type == "URL") null else it.selectedMovieId, targetUrl = if(event.type == "MOVIE") "" else it.targetUrl)
            }
            is BannerEditEvent.TargetUrlChanged -> _state.update { it.copy(targetUrl = event.url) }
            is BannerEditEvent.MovieSelected -> _state.update { it.copy(selectedMovieId = event.id) }
            is BannerEditEvent.PriorityChanged -> _state.update { it.copy(priorityStr = event.priority) }
            is BannerEditEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is BannerEditEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }
            is BannerEditEvent.SaveClicked -> saveBanner(event.context)
        }
    }

    private fun saveBanner(context: Context) {
        val st = _state.value

        if (st.actionType == "URL" && st.targetUrl.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập đường dẫn URL") }; return
        }
        if (st.actionType == "MOVIE" && st.selectedMovieId == null) {
            _state.update { it.copy(error = "Vui lòng chọn Phim") }; return
        }

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
            if (result.isSuccess) _state.update { it.copy(isSaving = false, isSuccess = true) }
            else _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
        }
    }
}