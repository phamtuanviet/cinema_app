package com.example.myapplication.presentation.screen.movie.booking

import com.example.myapplication.data.remote.dto.CinemaShowtimeDto
import com.example.myapplication.data.remote.dto.MovieDto

data class MovieBookingState(

    val isLoading : Boolean = true,
    val lat: Double? = null,
    val lng: Double? = null,
    val movie: MovieDto? = null,
    val dates: List<String> = emptyList(),
    val selectedDate: String? = null,
    val cinemas: List<CinemaShowtimeDto> = emptyList(),
    val error: String? = null
)