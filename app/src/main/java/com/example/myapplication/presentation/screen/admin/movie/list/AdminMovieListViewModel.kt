package com.example.myapplication.presentation.screen.admin.movie.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminMovieDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.domain.repository.AdminMovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminMovieListState(
    val movies: List<AdminMovieDto> = emptyList(),
    val isLoadingFirstPage: Boolean = true, // Loading lần đầu hoặc khi search
    val isPaginating: Boolean = false,      // Loading khi cuộn xuống dưới
    val error: String? = null,
    val searchQuery: String = "",
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

@HiltViewModel
class AdminMovieListViewModel @Inject constructor(
    private val repository: AdminMovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminMovieListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFirstPage()
    }

    fun onSearchQueryChange(query: String) {
        // 🔥 FIX LỖI SPAM: Chặn vòng lặp vô hạn nếu text không thực sự thay đổi
        if (_state.value.searchQuery == query) return

        _state.update { it.copy(searchQuery = query) }

        searchJob?.cancel()

        // Nếu người dùng xóa trắng thanh search, load lại trang 1 ngay lập tức
        if (query.isEmpty()) {
            loadFirstPage()
        } else {
            // Kỹ thuật Debounce: Chờ 500ms sau khi ngừng gõ mới gọi API
            searchJob = viewModelScope.launch {
                delay(500)
                loadFirstPage()
            }
        }
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingFirstPage = true,
                    error = null,
                    currentPage = 0,
                    isLastPage = false,
                    movies = emptyList() // Reset list khi tìm kiếm hoặc refresh
                )
            }

            val result = repository.getMovies(
                search = _state.value.searchQuery,
                page = 0
            )

            handleResult(result, isFirstPage = true)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        // Nếu đang loading, hoặc đã là trang cuối thì không gọi nữa
        if (currentState.isLoadingFirstPage || currentState.isPaginating || currentState.isLastPage) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isPaginating = true, error = null) }

            val nextPage = currentState.currentPage + 1
            val result = repository.getMovies(
                search = currentState.searchQuery,
                page = nextPage
            )

            handleResult(result, isFirstPage = false, nextPage = nextPage)
        }
    }

    private fun handleResult(
        result: Result<AdminPaginatedResponse<AdminMovieDto>>,
        isFirstPage: Boolean,
        nextPage: Int = 0
    ) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            _state.update { currentState ->
                val newMovies = if (isFirstPage) {
                    response?.content.orEmpty()
                } else {
                    currentState.movies + response?.content.orEmpty()
                }

                currentState.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    movies = newMovies,
                    currentPage = nextPage,
                    isLastPage = response?.isLast ?: true
                )
            }
        } else {
            _state.update {
                it.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    error = result.exceptionOrNull()?.message ?: "Lỗi tải dữ liệu"
                )
            }
        }
    }
}