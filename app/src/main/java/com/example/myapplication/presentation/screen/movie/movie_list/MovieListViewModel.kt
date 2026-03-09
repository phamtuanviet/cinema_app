package com.example.myapplication.presentation.screen.movie.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(

    private val repository: MovieRepository

) : ViewModel() {

    private val _state = MutableStateFlow(MovieListState())
    val state = _state.asStateFlow()

    init {
        loadBanner()
        loadNowShowing()
    }

    private fun loadBanner() = viewModelScope.launch {

        try {

            val banners = repository.getBanners()

            _state.update {
                it.copy(banners = banners)
            }

        } catch (e: Exception) {
            // banner lỗi vẫn không crash
        }

    }

    fun changeTab(tab: MovieTab) {

        _state.update { it.copy(selectedTab = tab) }

        when (tab) {

            MovieTab.NOW_SHOWING -> {

                if (_state.value.nowShowing.isEmpty()) {
                    loadNowShowing()
                }

            }

            MovieTab.COMING_SOON -> {

                if (_state.value.comingSoon.isEmpty()) {
                    loadComingSoon()
                }

            }

        }

    }

    private fun loadNowShowing() = viewModelScope.launch {

        _state.update { it.copy(isLoading = true, error = null) }

        try {

            val movies = repository.getNowShowingMovies()

            _state.update {

                it.copy(
                    nowShowing = movies,
                    isLoading = false
                )

            }

        } catch (e: Exception) {

            _state.update {

                it.copy(
                    error = "Không tải được phim",
                    isLoading = false
                )

            }

        }

    }

    private fun loadComingSoon() = viewModelScope.launch {

        _state.update { it.copy(isLoading = true, error = null) }

        try {

            val movies = repository.getComingSoonMovies()

            _state.update {

                it.copy(
                    comingSoon = movies,
                    isLoading = false
                )

            }

        } catch (e: Exception) {

            _state.update {

                it.copy(
                    error = "Không tải được phim",
                    isLoading = false
                )

            }

        }

    }

}