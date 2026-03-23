package com.example.myapplication.presentation.screen.movie.movie_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.BannerDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.domain.repository.BannerRepository
import com.example.myapplication.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val bannerRepository: BannerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieListState())
    val state = _state.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() = viewModelScope.launch {

        _state.update { it.copy(isLoading = true, error = null) }

        supervisorScope {
            val bannersDeferred = async { bannerRepository.getBanners() }
            val nowShowingDeferred = async { movieRepository.getNowShowingMovies() }

            val bannersResult = bannersDeferred.await()
            val nowShowingResult = nowShowingDeferred.await()

            _state.update {
                it.copy(
                    banners = bannersResult.getOrElse { emptyList() },
                    nowShowing = nowShowingResult.getOrElse { emptyList() },
                    error = bannersResult.exceptionOrNull()?.message
                        ?: nowShowingResult.exceptionOrNull()?.message,
                    isLoading = false
                )
            }
        }
    }

    fun changeTab(tab: MovieTab) {
        _state.update { it.copy(selectedTab = tab, isLoading = true, error = null) }

        when (tab) {
            MovieTab.NOW_SHOWING -> {
                if (_state.value.nowShowing.isEmpty()) {
                    loadNowShowing()
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }

            MovieTab.COMING_SOON -> {
                if (_state.value.comingSoon.isEmpty()) {
                    loadComingSoon()
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    /** Load nowShowing movies */
    private fun loadNowShowing() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        val result: Result<List<MovieDto>> = movieRepository.getNowShowingMovies()

        _state.update {
            it.copy(
                nowShowing = result.getOrElse { emptyList() },
                error = result.exceptionOrNull()?.message ?: it.error,
                isLoading = false
            )
        }
    }

    /** Load comingSoon movies */
    private fun loadComingSoon() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        val result: Result<List<MovieDto>> = movieRepository.getComingSoonMovies()

        Log.d("MovieListViewModel1", "Coming Soon Movies: ${result.getOrNull()}")


        _state.update {
            it.copy(
                comingSoon = result.getOrElse { emptyList() },
                error = result.exceptionOrNull()?.message ?: it.error,
                isLoading = false
            )
        }
    }
}