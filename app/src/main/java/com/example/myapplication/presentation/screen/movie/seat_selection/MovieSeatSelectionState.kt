package com.example.myapplication.presentation.screen.movie.seat_selection

import com.example.myapplication.data.remote.dto.MovieDetailDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.SeatDto
import com.example.myapplication.data.remote.dto.SeatMapDto

data class MovieSeatSelectionState(

    val isLoading : Boolean = true,
    val seatMap: SeatMapDto? = null,
    val movie: MovieDto? = null,
    val seatHoldSessionId : String? = null,
    val selectedSeats: List<String> = emptyList(),
    val selectedSeatNames: List<String> = emptyList(),
    val totalPrice: Double = 0.0,
    val expiresAt: String? = null,
    val error: String? = null
)