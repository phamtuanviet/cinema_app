package com.example.myapplication.presentation.screen.movie.movie_list

import com.example.myapplication.domain.model.Movie

data class MovieListState (
    val isLoading: Boolean = false,

    val error: String? = null,

    val banners: List<String> = emptyList(),

    val nowShowing: List<Movie> = emptyList(),

    val comingSoon: List<Movie> = emptyList(),

    val selectedTab: MovieTab = MovieTab.NOW_SHOWING
)