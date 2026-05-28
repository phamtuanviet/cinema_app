package com.example.myapplication.presentation.screen.movie.movie_list

import com.example.myapplication.data.remote.dto.BannerDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.domain.model.Movie

data class MovieListState(
    val isLoading: Boolean = false,       // Loading lúc mới vào hoặc đổi tab
    val isFetchingMore: Boolean = false,  // Loading cái xoay xoay ở dưới cùng khi cuộn
    val error: String? = null,
    val banners: List<BannerDto> = emptyList(),

    val movies: List<MovieDto> = emptyList(), //
    val selectedTab: MovieTab = MovieTab.NOW_SHOWING,
    val searchQuery: String = "",

    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)