package com.example.myapplication.presentation.screen.movie.movie_detail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.orEmpty

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    fun loadMovie(movieId: String) {
        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            try {

                val movie = repository.getMovie(movieId)

                _state.value = _state.value.copy(
                    isLoading = false,
                    movie = movie.getOrNull()
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}