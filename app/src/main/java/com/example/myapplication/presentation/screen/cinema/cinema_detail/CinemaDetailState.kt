package com.example.myapplication.presentation.screen.cinema.cinema_detail

import com.example.myapplication.data.remote.dto.CinemaShowtimeResponse

data class CinemaDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val dates: List<String> = emptyList(),
    val selectedDate: String? = null,

    val showtimes: CinemaShowtimeResponse? = null
)