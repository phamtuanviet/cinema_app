package com.example.myapplication.presentation.screen.admin.movie.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminGenreDto
import com.example.myapplication.data.remote.dto.AdminMovieUpdateRequest
import com.example.myapplication.domain.repository.AdminGenreRepository
import com.example.myapplication.domain.repository.AdminMovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminMovieEditState(
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    // Form data & Error states
    val title: String = "",
    val titleError: String? = null,

    val description: String = "",

    val durationMinutes: String = "",
    val durationError: String? = null,

    val releaseDate: String = "",
    val releaseDateError: String? = null,

    val basePrice: String = "",
    val priceError: String? = null,

    val trailerUrl: String = "",
    val language: String = "Tiếng Việt",
    val ageRating: String = "P",
    val isActive: Boolean = true,

    val currentPosterUrl: String? = null,
    val newPosterUri: Uri? = null,

    val availableGenres: List<AdminGenreDto> = emptyList(),
    val selectedGenres: List<AdminGenreDto> = emptyList(),
    val newGenres: List<String> = emptyList()
)

@HiltViewModel
class AdminMovieEditViewModel @Inject constructor(
    private val movieRepository: AdminMovieRepository,
    private val genreRepository: AdminGenreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: String = checkNotNull(savedStateHandle["movieId"])
    private val _state = MutableStateFlow(AdminMovieEditState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true, error = null) }

            val genresDeferred = async { genreRepository.getAllGenres() }
            val movieDeferred = async { movieRepository.getMovieById(movieId) }

            val genresResult = genresDeferred.await()
            val movieResult = movieDeferred.await()

            if (movieResult.isSuccess && genresResult.isSuccess) {
                val movie = movieResult.getOrNull()!!
                val allGenres = genresResult.getOrNull().orEmpty()

                _state.update {
                    it.copy(
                        isLoadingData = false,
                        availableGenres = allGenres,
                        title = movie.title,
                        description = movie.description ?: "",
                        durationMinutes = movie.durationMinutes?.toString() ?: "",
                        releaseDate = movie.releaseDate ?: "",
                        basePrice = movie.basePrice?.toLong()?.toString() ?: "",
                        trailerUrl = movie.trailerUrl ?: "",
                        language = movie.language ?: "Tiếng Việt",
                        ageRating = movie.ageRating ?: "P",
                        isActive = movie.isActive,
                        currentPosterUrl = movie.posterUrl,
                        selectedGenres = movie.genres
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoadingData = false,
                        error = movieResult.exceptionOrNull()?.message ?: genresResult.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    fun onEvent(event: MovieEditEvent) {
        when (event) {
            // Cập nhật text và XÓA LỖI ngay khi người dùng bắt đầu gõ lại
            is MovieEditEvent.TitleChanged -> _state.update { it.copy(title = event.title, titleError = null) }
            is MovieEditEvent.DurationChanged -> _state.update { it.copy(durationMinutes = event.duration, durationError = null) }
            is MovieEditEvent.BasePriceChanged -> _state.update { it.copy(basePrice = event.price, priceError = null) }
            is MovieEditEvent.ReleaseDateChanged -> _state.update { it.copy(releaseDate = event.date, releaseDateError = null) }

            is MovieEditEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is MovieEditEvent.TrailerUrlChanged -> _state.update { it.copy(trailerUrl = event.url) }
            is MovieEditEvent.LanguageChanged -> _state.update { it.copy(language = event.lang) }
            is MovieEditEvent.AgeRatingChanged -> _state.update { it.copy(ageRating = event.rating) }
            is MovieEditEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is MovieEditEvent.PosterPicked -> _state.update { it.copy(newPosterUri = event.uri) }
            is MovieEditEvent.GenreToggled -> {
                val current = _state.value.selectedGenres.toMutableList()
                if (current.contains(event.genre)) current.remove(event.genre) else current.add(event.genre)
                _state.update { it.copy(selectedGenres = current) }
            }
            is MovieEditEvent.NewGenreAdded -> {
                val currentNew = _state.value.newGenres.toMutableList()
                if (event.name.isNotBlank() && !currentNew.contains(event.name)) currentNew.add(event.name.trim())
                _state.update { it.copy(newGenres = currentNew) }
            }
            is MovieEditEvent.NewGenreRemoved -> {
                val currentNew = _state.value.newGenres.toMutableList()
                currentNew.remove(event.name)
                _state.update { it.copy(newGenres = currentNew) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun updateMovie(context: Context) {
        val st = _state.value
        var isValid = true

        // 1. Kiểm tra ảnh (Bắt buộc phải có ảnh cũ hoặc ảnh mới)
        if (st.currentPosterUrl == null && st.newPosterUri == null) {
            _state.update { it.copy(error = "Vui lòng cung cấp ảnh Poster phim!") }
            return
        }

        // 2. Kiểm tra Thể loại
        if (st.selectedGenres.isEmpty() && st.newGenres.isEmpty()) {
            _state.update { it.copy(error = "Vui lòng chọn ít nhất 1 thể loại phim!") }
            return
        }

        // 3. Kiểm tra các TextFields
        if (st.title.isBlank()) {
            _state.update { it.copy(titleError = "Tên phim không được để trống") }
            isValid = false
        }

        val duration = st.durationMinutes.toIntOrNull()
        if (duration == null || duration <= 0) {
            _state.update { it.copy(durationError = "Thời lượng phải là số lớn hơn 0") }
            isValid = false
        }

        val price = st.basePrice.toDoubleOrNull()
        if (price == null || price < 0) {
            _state.update { it.copy(priceError = "Giá vé không hợp lệ") }
            isValid = false
        }

        if (st.releaseDate.isBlank()) {
            _state.update { it.copy(releaseDateError = "Vui lòng chọn ngày khởi chiếu") }
            isValid = false
        }

        // Nếu có bất kỳ lỗi nào ở trên, DỪNG LẠI
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminMovieUpdateRequest(
                title = st.title.trim(),
                description = st.description.trim().takeIf { it.isNotEmpty() },
                durationMinutes = duration,
                releaseDate = st.releaseDate,
                basePrice = price,
                ageRating = st.ageRating,
                language = st.language,
                trailerUrl = st.trailerUrl.takeIf { it.isNotEmpty() },
                isActive = st.isActive,
                genreIds = st.selectedGenres.map { it.id },
                newGenres = st.newGenres
            )

            val result = movieRepository.updateMovie(movieId, request, st.newPosterUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Cập nhật thất bại") }
            }
        }
    }
}
sealed class MovieEditEvent {
    data class TitleChanged(val title: String) : MovieEditEvent()
    data class DescriptionChanged(val desc: String) : MovieEditEvent()
    data class DurationChanged(val duration: String) : MovieEditEvent()
    data class ReleaseDateChanged(val date: String) : MovieEditEvent()
    data class BasePriceChanged(val price: String) : MovieEditEvent()
    data class TrailerUrlChanged(val url: String) : MovieEditEvent()
    data class LanguageChanged(val lang: String) : MovieEditEvent()
    data class AgeRatingChanged(val rating: String) : MovieEditEvent()
    data class IsActiveChanged(val isActive: Boolean) : MovieEditEvent()
    data class PosterPicked(val uri: Uri?) : MovieEditEvent()
    data class GenreToggled(val genre: AdminGenreDto) : MovieEditEvent()
    data class NewGenreAdded(val name: String) : MovieEditEvent()
    data class NewGenreRemoved(val name: String) : MovieEditEvent()
}