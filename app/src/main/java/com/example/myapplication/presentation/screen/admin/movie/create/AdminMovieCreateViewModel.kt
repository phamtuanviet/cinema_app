package com.example.myapplication.presentation.screen.admin.movie.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminGenreDto
import com.example.myapplication.data.remote.dto.AdminMovieCreateRequest
import com.example.myapplication.domain.repository.AdminGenreRepository
import com.example.myapplication.domain.repository.AdminMovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminMovieCreateViewModel @Inject constructor(
    private val movieRepository: AdminMovieRepository,
    private val genreRepository: AdminGenreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminMovieCreateState())
    val state = _state.asStateFlow()

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            val result = genreRepository.getAllGenres()
            if (result.isSuccess) _state.update { it.copy(availableGenres = result.getOrNull().orEmpty()) }
        }
    }

    fun onEvent(event: MovieCreateEvent) {
        when (event) {
            // Khi người dùng gõ lại, lập tức xóa dòng báo lỗi đỏ đi
            is MovieCreateEvent.TitleChanged -> _state.update { it.copy(title = event.title, titleError = null) }
            is MovieCreateEvent.DurationChanged -> _state.update { it.copy(durationMinutes = event.duration, durationError = null) }
            is MovieCreateEvent.BasePriceChanged -> _state.update { it.copy(basePrice = event.price, priceError = null) }
            is MovieCreateEvent.ReleaseDateChanged -> _state.update { it.copy(releaseDate = event.date, releaseDateError = null) }

            is MovieCreateEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is MovieCreateEvent.TrailerUrlChanged -> _state.update { it.copy(trailerUrl = event.url) }
            is MovieCreateEvent.AgeRatingChanged -> _state.update { it.copy(ageRating = event.rating) }
            is MovieCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is MovieCreateEvent.SendNotificationChanged -> _state.update { it.copy(sendNotification = event.sendNotification) }
            is MovieCreateEvent.PosterPicked -> _state.update { it.copy(posterUri = event.uri) }
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
            is MovieCreateEvent.LanguageChanged -> _state.update { it.copy(language = event.lang) }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun createMovie(context: Context) {
        val st = _state.value
        var isValid = true

        // 1. Kiểm tra Poster & Thể loại (Báo lỗi chung qua Toast)
        if (st.posterUri == null) {
            _state.update { it.copy(error = "Vui lòng chọn ảnh Poster cho bộ phim!") }
            return
        }
        if (st.selectedGenres.isEmpty() && st.newGenres.isEmpty()) {
            _state.update { it.copy(error = "Vui lòng chọn ít nhất 1 thể loại phim!") }
            return
        }

        // 2. Kiểm tra Tên phim
        if (st.title.isBlank()) {
            _state.update { it.copy(titleError = "Tên phim không được để trống") }
            isValid = false
        }

        // 3. Kiểm tra Thời lượng (Phải là số và > 0)
        val duration = st.durationMinutes.toIntOrNull()
        if (duration == null || duration <= 0) {
            _state.update { it.copy(durationError = "Thời lượng phải là số lớn hơn 0") }
            isValid = false
        }

        // 4. Kiểm tra Giá vé (Phải là số và >= 0)
        val price = st.basePrice.toDoubleOrNull()
        if (price == null || price < 0) {
            _state.update { it.copy(priceError = "Giá vé không hợp lệ") }
            isValid = false
        }

        // 5. Kiểm tra Ngày chiếu
        if (st.releaseDate.isBlank()) {
            _state.update { it.copy(releaseDateError = "Vui lòng chọn ngày khởi chiếu") }
            isValid = false
        }

        // NẾU CÓ LỖI -> DỪNG LẠI KHÔNG GỌI API
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val request = AdminMovieCreateRequest(
                title = st.title.trim(),
                description = st.description.trim().takeIf { it.isNotEmpty() },
                durationMinutes = duration,
                releaseDate = st.releaseDate,
                basePrice = price,
                ageRating = st.ageRating,
                language = st.language,
                trailerUrl = st.trailerUrl.takeIf { it.isNotEmpty() },
                isActive = st.isActive,
                sendNotification = st.sendNotification,
                genreIds = st.selectedGenres.map { it.id },
                newGenres = st.newGenres
            )

            val result = movieRepository.createMovie(request, st.posterUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Tạo phim thất bại. Vui lòng thử lại!") }
            }
        }
    }
}

sealed class MovieCreateEvent {
    data class TitleChanged(val title: String) : MovieCreateEvent()
    data class DescriptionChanged(val desc: String) : MovieCreateEvent()
    data class DurationChanged(val duration: String) : MovieCreateEvent()
    data class ReleaseDateChanged(val date: String) : MovieCreateEvent()
    data class BasePriceChanged(val price: String) : MovieCreateEvent()
    data class TrailerUrlChanged(val url: String) : MovieCreateEvent()
    data class LanguageChanged(val lang: String) : MovieCreateEvent()
    data class AgeRatingChanged(val rating: String) : MovieCreateEvent()
    data class IsActiveChanged(val isActive: Boolean) : MovieCreateEvent()
    data class SendNotificationChanged(val sendNotification: Boolean) : MovieCreateEvent()
    data class PosterPicked(val uri: Uri?) : MovieCreateEvent()
    data class GenreToggled(val genre: AdminGenreDto) : MovieCreateEvent()
    data class NewGenreAdded(val name: String) : MovieCreateEvent()
    data class NewGenreRemoved(val name: String) : MovieCreateEvent()
}