package com.example.myapplication.presentation.screen.cinema.cinema_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CinemaDetailViewModel @Inject constructor(
    private val cinemaRepository: CinemaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CinemaDetailState())
    val state: StateFlow<CinemaDetailState> = _state

    fun loadData(cinemaId: String) {
        viewModelScope.launch {
            Log.d("CinemaDetailViewModel", "loadData: $cinemaId")

            _state.update { it.copy(isLoading = true, error = null) }

            val result = cinemaRepository.getShowtimeDates(cinemaId)

            if (result.isSuccess) {
                val dates = result.getOrNull() ?: emptyList()

                val firstDate = dates.firstOrNull()

                _state.update {
                    it.copy(
                        dates = dates,
                        selectedDate = firstDate
                    )
                }

                if (firstDate != null) {
                    Log.d("CinemaDetailViewModel", "loadData: $cinemaId")

                    loadShowtimes(cinemaId, firstDate)
                } else {
                    _state.update { it.copy(isLoading = false) }
                }

            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    fun loadShowtimes(cinemaId: String, date: String) {

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            val result = cinemaRepository.getShowtimes(cinemaId, date)

            if (result.isSuccess) {
                val data = result.getOrNull()
                Log.d("CinemaDetailViewModel", "loadShowtimes: $data")

                _state.update {
                    it.copy(
                        isLoading = false,
                        showtimes = data,
                        selectedDate = date
                    )
                }

            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }
}