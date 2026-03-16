package com.example.myapplication.presentation.screen.movie.seat_selection

import com.example.myapplication.data.remote.dto.MovieDetailDto
import com.example.myapplication.data.remote.dto.SeatMapDto

data class MovieSeatSelectionState(

    val isLoadingMovie: Boolean = true,
    val isLoadingSeatMap: Boolean = true,

    val seatMap: SeatMapDto? = null,

    val movie: MovieDetailDto? = null,
    val sessionId : String? = null,

    val selectedSeats: Set<String> = emptySet<String>(),
    val totalPrice: Double = 0.0,
    val expiresAt: String? = null,
    val error: String? = null
)