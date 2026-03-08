package com.example.myapplication.presentation.screen.movie.movie_list

data class MovieListState (
    val isLoading: Boolean = true,
    val error: String? = ""
)