package com.example.myapplication.presentation.screen.movie.seat_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.enums.SeatStatus
import com.example.myapplication.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieSeatSelectionViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MovieSeatSelectionState())
    val state = _state.asStateFlow()

    fun loadMovie(movieId: String) {

        viewModelScope.launch {

            _state.update { it.copy(isLoadingMovie = true) }

            try {

                val movie = repository.getMovieDetail(movieId)

                _state.update {
                    it.copy(
                        movie = movie,
                        isLoadingMovie = false
                    )
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        isLoadingMovie = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun loadSeatMap(showtimeId: String) {

        viewModelScope.launch {

            _state.update {
                it.copy(isLoadingSeatMap = true)
            }

            val seatMap = repository.getSeatMap(showtimeId)

            val selectedSeats = seatMap.rows
                .flatMap { it.seats }
                .filter { it.status == SeatStatus.HELD_BY_ME }
                .map { it.id }
                .toSet()

            val totalPrice = seatMap.rows
                .flatMap { it.seats }
                .filter { it.status == SeatStatus.HELD_BY_ME }
                .sumOf { it.price }

            _state.update {

                it.copy(
                    isLoadingSeatMap = false,
                    seatMap = seatMap,
                    selectedSeats = selectedSeats,
                    totalPrice = totalPrice,
                    expiresAt = seatMap.expiresAt
                )
            }
        }
    }

    fun toggleSeat(
        showtimeId: String,
        seatId: String
    ) {

        viewModelScope.launch {

            val seat = _state.value.seatMap
                ?.rows
                ?.flatMap { it.seats }
                ?.find { it.id == seatId }
                ?: return@launch

            when (seat.status) {

                SeatStatus.AVAILABLE -> {

                    if (_state.value.selectedSeats.size >= 8) {

                        _state.update {
                            it.copy(error = "You can select up to 8 seats")
                        }

                        return@launch
                    }

                    holdSeat(showtimeId, seatId)
                }

                SeatStatus.HELD_BY_ME -> cancelSeat(seatId)

                else -> {}
            }
        }
    }

    private suspend fun holdSeat(
        showtimeId: String,
        seatId: String
    ) {

        val response = repository.holdSeat(showtimeId, seatId)

        _state.update { current ->

            val newSeats = current.selectedSeats + seatId

            val updatedSeatMap = current.seatMap?.let { map ->

                map.copy(
                    rows = map.rows.map { row ->
                        row.copy(
                            seats = row.seats.map { seat ->
                                if (seat.id == seatId) {
                                    seat.copy(status = SeatStatus.HELD_BY_ME)
                                } else seat
                            }
                        )
                    }
                )
            }

            current.copy(
                seatMap = updatedSeatMap,
                selectedSeats = newSeats,
                totalPrice = current.totalPrice + response.price,

                sessionId = response.sessionId,
                expiresAt = response.expiresAt
            )
        }
    }

    private suspend fun cancelSeat(
        seatId: String
    ) {

        val price = _state.value.seatMap
            ?.rows
            ?.flatMap { it.seats }
            ?.find { it.id == seatId }
            ?.price ?: 0.0

        repository.cancelSeat(seatId)

        _state.update { current ->

            val newSeats = current.selectedSeats - seatId

            val updatedSeatMap = current.seatMap?.let { map ->

                map.copy(
                    rows = map.rows.map { row ->
                        row.copy(
                            seats = row.seats.map { seat ->
                                if (seat.id == seatId) {
                                    seat.copy(status = SeatStatus.AVAILABLE)
                                } else seat
                            }
                        )
                    }
                )
            }

            current.copy(
                seatMap = updatedSeatMap,
                selectedSeats = newSeats,
                totalPrice = current.totalPrice - price
            )
        }
    }
}