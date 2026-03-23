package com.example.myapplication.presentation.screen.movie.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.MovieRepository
import com.example.myapplication.domain.repository.ShowtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieBookingViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val showtimeRepository: ShowtimeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieBookingState())
    val state: StateFlow<MovieBookingState> = _state

    private var currentMovieId: String? = null

    fun load(movieId: String) {
        currentMovieId = movieId

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            val movieDeferred = async { movieRepository.getMovie(movieId) }
            val datesDeferred = async { showtimeRepository.getShowDates(movieId) }

            // 👉 xử lý dates trước
            val dateResult = datesDeferred.await()

            if (dateResult.isSuccess) {
                val dates = dateResult.getOrNull().orEmpty()
                val selectedDate = dates.firstOrNull()

                _state.update {
                    it.copy(
                        dates = dates,
                        selectedDate = selectedDate
                    )
                }

                val lat = _state.value.lat
                val lng = _state.value.lng

                if (lat != null && lng != null && selectedDate != null) {
                    loadCinemas(movieId, selectedDate, lat, lng)
                }

            } else {
                _state.update {
                    it.copy(error = dateResult.exceptionOrNull()?.message)
                }
            }

            // 👉 movie
            val movieResult = movieDeferred.await()

            _state.update {
                it.copy(
                    movie = movieResult.getOrNull(),
                    isLoading = false
                )
            }
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        _state.update { it.copy(lat = lat, lng = lng) }

        val movieId = currentMovieId ?: return
        val date = _state.value.selectedDate ?: return

        loadCinemas(movieId, date, lat, lng)
    }

    fun selectDate(date: String) {
        _state.update { it.copy(selectedDate = date) }

        val movieId = currentMovieId ?: return
        val lat = _state.value.lat ?: return
        val lng = _state.value.lng ?: return

        loadCinemas(movieId, date, lat, lng)
    }

    private fun loadCinemas(
        movieId: String,
        date: String,
        lat: Double,
        lng: Double
    ) {
        viewModelScope.launch {

            // 👉 chỉ loading nếu chưa có data
            _state.update {
                it.copy(isLoading = it.cinemas.isEmpty())
            }

            val result = showtimeRepository.getCinemaShowtimes(
                movieId, date, lat, lng
            )

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        cinemas = result.getOrNull().orEmpty(),
                        isLoading = false
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        error = result.exceptionOrNull()?.message,
                        isLoading = false
                    )
                }
            }
        }
    }
}