package com.example.myapplication.presentation.screen.movie.movie_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.BannerDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.domain.repository.BannerRepository
import com.example.myapplication.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
    private var searchJob: Job? = null

    init {
        loadBanners()
        loadMovies(isRefresh = true) // Load trang đầu tiên
    }

    private fun loadBanners() = viewModelScope.launch {
        val result = bannerRepository.getBanners()
        _state.update { it.copy(banners = result.getOrElse { emptyList() }) }
    }

    // 🔥 HÀM CỐT LÕI ĐỂ LOAD PHIM
    private fun loadMovies(isRefresh: Boolean = false) {
        val currentState = _state.value

        // 1. Chặn gọi API nếu đang load dở hoặc đã hết trang
        if (currentState.isLastPage && !isRefresh) return
        if (currentState.isFetchingMore || (currentState.isLoading && isRefresh)) return

        // 2. 🔥 QUAN TRỌNG: Cập nhật trạng thái ĐANG LOAD ngay lập tức (Đồng bộ)
        // Để chặn đứng hiện tượng gọi API nhiều lần do cuộn nhanh
        _state.update {
            it.copy(
                isLoading = if (isRefresh) true else it.isLoading,
                isFetchingMore = if (!isRefresh) true else it.isFetchingMore,
                error = null,
                currentPage = if (isRefresh) 0 else it.currentPage,
                movies = if (isRefresh) emptyList() else it.movies
            )
        }

        viewModelScope.launch {
            val pageToLoad = if (isRefresh) 0 else currentState.currentPage + 1
            // Lấy search query mới nhất
            val query = _state.value.searchQuery.takeIf { it.isNotBlank() }

            val result = if (currentState.selectedTab == MovieTab.NOW_SHOWING) {
                movieRepository.getNowShowingMovies(query, pageToLoad)
            } else {
                movieRepository.getComingSoonMovies(query, pageToLoad)
            }

            result.fold(
                onSuccess = { pageResponse ->
                    _state.update {
                        val newList =
                            if (isRefresh) pageResponse.content else it.movies + pageResponse.content
                        it.copy(
                            isLoading = false,
                            isFetchingMore = false,
                            // 🔥 Bọc lót: Lọc bỏ các phim bị trùng lặp ID
                            movies = newList.distinctBy { movie -> movie.id },
                            currentPage = pageToLoad,
                            isLastPage = pageResponse.last
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isLoading = false, isFetchingMore = false, error = e.message)
                    }
                }
            )
        }
    }

    // Khi cuộn xuống cuối danh sách
    fun loadNextPage() {
        loadMovies(isRefresh = false)
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            loadMovies(isRefresh = true)
        }
    }

    // Khi đổi Tab -> Reset page = 0
    fun changeTab(tab: MovieTab) {
        if (_state.value.selectedTab == tab) return
        _state.update { it.copy(selectedTab = tab) }
        loadMovies(isRefresh = true)
    }
}