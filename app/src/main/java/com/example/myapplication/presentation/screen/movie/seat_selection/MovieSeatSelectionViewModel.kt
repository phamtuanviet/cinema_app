package com.example.myapplication.presentation.screen.movie.seat_selection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.CancelSeatCoupleResponse
import com.example.myapplication.data.remote.dto.CancelSeatResponse
import com.example.myapplication.data.remote.dto.HoldSeatCoupleResponse
import com.example.myapplication.data.remote.dto.HoldSeatResponse
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.SeatMapDto
import com.example.myapplication.data.remote.enums.SeatStatus
import com.example.myapplication.domain.repository.MovieRepository
import com.example.myapplication.domain.repository.SeatRepository
import com.example.myapplication.domain.repository.ShowtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieSeatSelectionViewModel @Inject constructor(
    private val seatRepository: SeatRepository,
    private val showtimeRepository: ShowtimeRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MovieSeatSelectionState())
    val state = _state.asStateFlow()




    fun loadData(showtimeId: String) {
        viewModelScope.launch {
            // Bắt đầu loading chung
            _state.update { it.copy(isLoading = true, error = null) }

            coroutineScope {
                // Chạy song song 2 API mà không gói runCatching nữa
                val movieDeferred = async { showtimeRepository.getMovieByShowtime(showtimeId) } // trả về Result<MovieDto>
                val seatMapDeferred = async { seatRepository.getSeatMap(showtimeId) } // trả về Result<SeatMap>

                // Xử lý kết quả movie
                val movieResult = movieDeferred.await()
                movieResult.onSuccess { movie ->
                    _state.update { it.copy(movie = movie) }
                }.onFailure { e ->
                    _state.update { it.copy(error = e.message ?: "Load movie failed") }
                }

                // Xử lý kết quả seat map
                val seatMapResult = seatMapDeferred.await()
                seatMapResult.onSuccess { seatMap ->
                    Log.d("SeatMap", seatMap.toString())

                    val seats = seatMap.rows.flatMap { it.seats }

                    val selectedSeats = seats
                        .filter { it.status == SeatStatus.HOLD_BY_ME }
                        .map { it.id }

                    val totalPrice = seats
                        .filter { it.status == SeatStatus.HOLD_BY_ME }
                        .sumOf { it.price }

                    val selectedSeatNames = selectedSeats.mapNotNull { id ->
                        seats.find { it.id == id }?.let { "${it.seatRow}${it.seatNumber}" }
                    }

                    _state.update {
                        it.copy(
                            seatMap = seatMap,
                            selectedSeats = selectedSeats,
                            selectedSeatNames = selectedSeatNames,
                            totalPrice = totalPrice,
                            expiresAt = seatMap.expiresAt,
                            seatHoldSessionId = seatMap.seatHoldSessionId
                        )
                    }
                }.onFailure { e ->
                    _state.update { it.copy(error = e.message ?: "Load seat map failed") }
                }

                // Kết thúc loading
                _state.update { it.copy(isLoading = false) }
            }
        }
    }


    private fun mapSeatIdsToDisplay(
        selectedSeatIds: List<String>,
        seatMap: SeatMapDto?
    ): List<String> {
        if (seatMap == null) return emptyList()

        val seatMapById = seatMap.rows
            .flatMap { it.seats }
            .associateBy { it.id }

        return selectedSeatIds.mapNotNull { id ->
            seatMapById[id]?.let { seat ->
                "${seat.seatRow}${seat.seatNumber}"
            }
        }
    }

    fun toggleSeats(
        showtimeId: String,
        seatIds: List<String>
    ) {
        viewModelScope.launch {

            val current = _state.value

            val isSelected = seatIds.all { current.selectedSeats.contains(it) }

            if (!isSelected && current.selectedSeats.size + seatIds.size > 8) {
                _state.update {
                    it.copy(error = "You can select up to 8 seats")
                }
                return@launch
            }

            val result = if (seatIds.size == 2) {
                if (isSelected) {
                    seatRepository.cancelSeatCouple(showtimeId, seatIds[0], seatIds[1])
                } else {
                    seatRepository.holdSeatCouple(showtimeId, seatIds[0], seatIds[1])
                }
            } else {
                if (isSelected) {
                    seatRepository.cancelSeat(showtimeId, seatIds[0])
                } else {
                    seatRepository.holdSeat(showtimeId, seatIds[0])
                }
            }

            result.onSuccess { response ->
                updateStateAfterToggle(seatIds, isSelected, response)
            }.onFailure { e ->
                _state.update {
                    it.copy(error = e.message ?: "Action failed")
                }
            }
        }
    }

    private fun updateStateAfterToggle(
        seatIds: List<String>,
        isSelected: Boolean,
        response: Any // có thể generic sau
    ) {

        _state.update { current ->

            val updatedSeatMap = current.seatMap?.let { map ->
                map.copy(
                    rows = map.rows.map { row ->
                        row.copy(
                            seats = row.seats.map { seat ->
                                if (seatIds.contains(seat.id)) {
                                    seat.copy(
                                        status = if (isSelected)
                                            SeatStatus.AVAILABLE
                                        else
                                            SeatStatus.HOLD_BY_ME
                                    )
                                } else seat
                            }
                        )
                    }
                )
            }

            val newSelectedSeats =
                if (isSelected) {
                    current.selectedSeats - seatIds
                } else {
                    current.selectedSeats + seatIds
                }

            val selectedSeatNames = mapSeatIdsToDisplay(
                newSelectedSeats,
                updatedSeatMap
            )

            val priceDelta = extractPrice(response)

            val newTotalPrice =
                if (isSelected) {
                    current.totalPrice - priceDelta
                } else {
                    current.totalPrice + priceDelta
                }

            current.copy(
                seatMap = updatedSeatMap,
                selectedSeats = newSelectedSeats,
                selectedSeatNames = selectedSeatNames,
                totalPrice = newTotalPrice,
                seatHoldSessionId = extractSessionId(response),
                expiresAt = extractExpire(response),
                error = null
            )
        }
    }

    private fun extractPrice(response: Any): Double {
        return when (response) {
            is HoldSeatResponse -> response.price
            is CancelSeatResponse -> response.price
            is HoldSeatCoupleResponse -> response.price
            is CancelSeatCoupleResponse -> response.price
            else -> 0.0
        }
    }

    private fun extractSessionId(response: Any): String? {
        return when (response) {
            is HoldSeatResponse -> response.seatHoldSessionId
            is HoldSeatCoupleResponse -> response.seatHoldSessionId
            else -> null
        }
    }

    private fun extractExpire(response: Any): String? {
        return when (response) {
            is HoldSeatResponse -> response.expiresAt
            is HoldSeatCoupleResponse -> response.expiresAt
            else -> null
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}