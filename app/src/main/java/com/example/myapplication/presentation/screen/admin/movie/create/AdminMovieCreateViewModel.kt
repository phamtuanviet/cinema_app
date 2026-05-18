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
            if (result.isSuccess) {
                _state.update { it.copy(availableGenres = result.getOrNull().orEmpty()) }
            }
        }
    }

    fun onEvent(event: MovieCreateEvent) {
        when (event) {
            is MovieCreateEvent.SendNotificationChanged -> _state.update { it.copy(sendNotification = event.sendNotification) }
            is MovieCreateEvent.TitleChanged -> _state.update { it.copy(title = event.title, error = null) }
            is MovieCreateEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is MovieCreateEvent.DurationChanged -> _state.update { it.copy(durationMinutes = event.duration) }
            is MovieCreateEvent.ReleaseDateChanged -> _state.update { it.copy(releaseDate = event.date) }
            is MovieCreateEvent.BasePriceChanged -> _state.update { it.copy(basePrice = event.price) }
            is MovieCreateEvent.TrailerUrlChanged -> _state.update { it.copy(trailerUrl = event.url) }
            is MovieCreateEvent.LanguageChanged -> _state.update { it.copy(language = event.lang) }
            is MovieCreateEvent.AgeRatingChanged -> _state.update { it.copy(ageRating = event.rating) }
            is MovieCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
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
        }
    }

    fun createMovie(context: Context) {
        val currentState = _state.value
        if (currentState.title.isBlank() || currentState.basePrice.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập Tên phim và Giá vé gốc") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val request = AdminMovieCreateRequest(
                title = currentState.title.trim(),
                description = currentState.description.trim().takeIf { it.isNotEmpty() },
                durationMinutes = currentState.durationMinutes.toIntOrNull(),
                releaseDate = currentState.releaseDate.takeIf { it.isNotEmpty() }, // Format YYYY-MM-DD
                basePrice = currentState.basePrice.toDoubleOrNull(),
                ageRating = currentState.ageRating,
                language = currentState.language,
                trailerUrl = currentState.trailerUrl.takeIf { it.isNotEmpty() },
                isActive = currentState.isActive,
                sendNotification = currentState.sendNotification,
                genreIds = currentState.selectedGenres.map { it.id },
                newGenres = currentState.newGenres
            )

            val result = movieRepository.createMovie(request, currentState.posterUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
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