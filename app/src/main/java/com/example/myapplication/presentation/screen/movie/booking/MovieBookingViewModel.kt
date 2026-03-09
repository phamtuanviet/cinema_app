package com.example.myapplication.presentation.screen.movie.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieBookingViewModel @Inject constructor(
    private val repository: MovieRepository

) : ViewModel() {

    private val _state = MutableStateFlow(MovieBookingState())
    val state = _state.asStateFlow()

    fun loadMovie(movieId: String) {

        viewModelScope.launch {

            _state.update { it.copy(isLoadingMovie = true) }

            try {

                val movie = repository.getMovie(movieId)

                _state.update {
                    it.copy(
                        movie = movie,
                        isLoadingMovie = false
                    )
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        error = e.message,
                        isLoadingMovie = false
                    )
                }
            }
        }
    }

    fun setLocation(lat: Double, lng: Double) {
        _state.update {
            it.copy(
                lat = lat,
                lng = lng
            )
        }
    }

    fun selectDate(

        date: String,
    ) {

        _state.update {
            it.copy(
                selectedDate = date,
            )
        }
    }

    fun loadDates(movieId: String) {

        viewModelScope.launch {

            _state.update { it.copy(isLoadingDates = true) }

            try {

                val dates = repository.getAvailableDates(movieId)

                _state.update {
                    it.copy(
                        dates = dates,
                        selectedDate = dates.firstOrNull(),
                        isLoadingDates = false
                    )
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        error = e.message,
                        isLoadingDates = false
                    )
                }
            }
        }
    }

    fun loadCinemas(
        movieId: String,
        date: String,
    ) {

        viewModelScope.launch {

            _state.update { it.copy(isLoadingCinemas = true) }

            try {

                val cinemas = repository.getCinemas(
                    movieId,
                    date,
                    state.value.lat,
                    state.value.lng
                )

                _state.update {
                    it.copy(
                        cinemas = cinemas,
                        isLoadingCinemas = false
                    )
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        error = e.message,
                        isLoadingCinemas = false
                    )
                }
            }
        }
    }
}