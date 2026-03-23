package com.example.myapplication.presentation.screen.movie.movie_list

import com.example.myapplication.data.remote.dto.BannerDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.domain.model.Movie

data class MovieListState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val banners: List<BannerDto> = emptyList(),
    val nowShowing: List<MovieDto> = emptyList(),
    val comingSoon: List<MovieDto> = emptyList(),

    val selectedTab: MovieTab = MovieTab.NOW_SHOWING
)