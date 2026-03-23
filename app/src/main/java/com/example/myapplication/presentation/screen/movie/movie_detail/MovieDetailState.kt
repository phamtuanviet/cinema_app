package com.example.myapplication.presentation.screen.movie.movie_detail

import com.example.myapplication.data.remote.dto.MovieDetailDto
import com.example.myapplication.data.remote.dto.MovieDto

data class MovieDetailState(
    val isLoading: Boolean = false,
    val movie: MovieDto? = null,
    val error: String? = null
)