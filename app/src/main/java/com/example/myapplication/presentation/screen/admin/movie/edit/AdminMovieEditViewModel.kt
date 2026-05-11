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
import com.example.myapplication.presentation.screen.admin.movie.create.MovieCreateEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminMovieEditState(
    val isLoadingData: Boolean = true, // Loading khi tải dữ liệu phim lúc đầu
    val isSaving: Boolean = false,     // Loading khi ấn nút Lưu
    val isSuccess: Boolean = false,
    val error: String? = null,

    // Form data
    val title: String = "",
    val description: String = "",
    val durationMinutes: String = "",
    val releaseDate: String = "",
    val basePrice: String = "",
    val trailerUrl: String = "",
    val language: String = "Tiếng Việt",
    val ageRating: String = "P",
    val isActive: Boolean = true,

    val currentPosterUrl: String? = null, // Ảnh hiện tại từ DB
    val newPosterUri: Uri? = null,        // Ảnh mới nếu user chọn đè lên

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

    // Tự động lấy movieId từ NavArgument
    private val movieId: String = checkNotNull(savedStateHandle["movieId"])

    private val _state = MutableStateFlow(AdminMovieEditState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true, error = null) }

            // Gọi song song để tiết kiệm thời gian
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
                        // Đổ dữ liệu cũ vào form
                        title = movie.title,
                        description = movie.description ?: "",
                        durationMinutes = movie.durationMinutes?.toString() ?: "",
                        releaseDate = movie.releaseDate ?: "",
                        basePrice = movie.basePrice?.toLong()?.toString() ?: "", // Convert số thập phân về string
                        trailerUrl = movie.trailerUrl ?: "",
                        language = movie.language ?: "Tiếng Việt",
                        ageRating = movie.ageRating ?: "P",
                        isActive = movie.isActive,
                        currentPosterUrl = movie.posterUrl,
                        selectedGenres = movie.genres // Map các thể loại phim đang có
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

    // Tái sử dụng MovieCreateEvent hoặc tạo MovieEditEvent tương tự
    fun onEvent(event: MovieCreateEvent) {
        when (event) {
            is MovieCreateEvent.TitleChanged -> _state.update { it.copy(title = event.title, error = null) }
            is MovieCreateEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is MovieCreateEvent.DurationChanged -> _state.update { it.copy(durationMinutes = event.duration) }
            is MovieCreateEvent.ReleaseDateChanged -> _state.update { it.copy(releaseDate = event.date) }
            is MovieCreateEvent.BasePriceChanged -> _state.update { it.copy(basePrice = event.price) }
            is MovieCreateEvent.TrailerUrlChanged -> _state.update { it.copy(trailerUrl = event.url) }
            is MovieCreateEvent.LanguageChanged -> _state.update { it.copy(language = event.lang) }
            is MovieCreateEvent.AgeRatingChanged -> _state.update { it.copy(ageRating = event.rating) }
            is MovieCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is MovieCreateEvent.PosterPicked -> _state.update { it.copy(newPosterUri = event.uri) } // Lưu vào newPosterUri
            is MovieCreateEvent.GenreToggled -> {
                val current = _state.value.selectedGenres.toMutableList()
                if (current.contains(event.genre)) current.remove(event.genre) else current.add(event.genre)
                _state.update { it.copy(selectedGenres = current) }
            }
            is MovieCreateEvent.NewGenreAdded -> {
                val currentNew = _state.value.newGenres.toMutableList()
                if (event.name.isNotBlank() && !currentNew.contains(event.name)) currentNew.add(event.name.trim())
                _state.update { it.copy(newGenres = currentNew) }
            }
            is MovieCreateEvent.NewGenreRemoved -> {
                val currentNew = _state.value.newGenres.toMutableList()
                currentNew.remove(event.name)
                _state.update { it.copy(newGenres = currentNew) }
            }
        }
    }

    fun updateMovie(context: Context) {
        val currentState = _state.value
        if (currentState.title.isBlank() || currentState.basePrice.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập Tên phim và Giá vé gốc") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminMovieUpdateRequest(
                title = currentState.title.trim(),
                description = currentState.description.trim().takeIf { it.isNotEmpty() },
                durationMinutes = currentState.durationMinutes.toIntOrNull(),
                releaseDate = currentState.releaseDate.takeIf { it.isNotEmpty() },
                basePrice = currentState.basePrice.toDoubleOrNull(),
                ageRating = currentState.ageRating,
                language = currentState.language,
                trailerUrl = currentState.trailerUrl.takeIf { it.isNotEmpty() },
                isActive = currentState.isActive,
                genreIds = currentState.selectedGenres.map { it.id },
                newGenres = currentState.newGenres
            )

            val result = movieRepository.updateMovie(movieId, request, currentState.newPosterUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}