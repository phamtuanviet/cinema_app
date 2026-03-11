package com.example.myapplication.presentation.screen.movie.movie_detail

import com.example.myapplication.data.remote.dto.MovieDetailDto

data class MovieDetailState(

    val isLoading: Boolean = false,

    val movie: MovieDetailDto? = null,

    val error: String? = null
)