package com.example.myapplication.presentation.screen.admin.showtime.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminShowtimeDto
import com.example.myapplication.data.remote.dto.ShowtimeTab
import com.example.myapplication.domain.repository.AdminShowtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminShowtimeListState(
    val showtimes: List<AdminShowtimeDto> = emptyList(),
    val isLoadingFirstPage: Boolean = true,
    val isPaginating: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val currentTab: ShowtimeTab = ShowtimeTab.UPCOMING, // Mặc định hiển thị Sắp chiếu

    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

@HiltViewModel
class AdminShowtimeListViewModel @Inject constructor(
    private val repository: AdminShowtimeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminShowtimeListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFirstPage()
    }

    // Khi người dùng gõ tìm kiếm
    fun onSearchQueryChange(query: String) {
        if (_state.value.searchQuery == query) return

        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500) // Debounce 0.5s
            loadFirstPage()
        }
    }

    // Khi người dùng bấm chuyển Tab (Sắp chiếu / Đang chiếu / Đã chiếu)
    fun onTabSelected(tab: ShowtimeTab) {
        if (_state.value.currentTab == tab) return

        _state.update { it.copy(currentTab = tab) }
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoadingFirstPage = true, error = null, currentPage = 0, isLastPage = false, showtimes = emptyList())
            }

            val currentState = _state.value
            val result = repository.getShowtimes(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                filter = currentState.currentTab.filterValue,
                page = 0
            )

            handleResult(result, isFirstPage = true)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isLoadingFirstPage || currentState.isPaginating || currentState.isLastPage) return

        viewModelScope.launch {
            _state.update { it.copy(isPaginating = true, error = null) }

            val nextPage = currentState.currentPage + 1
            val result = repository.getShowtimes(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                filter = currentState.currentTab.filterValue,
                page = nextPage
            )

            handleResult(result, isFirstPage = false, nextPage = nextPage)
        }
    }

    private fun handleResult(result: Result<AdminPaginatedResponse<AdminShowtimeDto>>, isFirstPage: Boolean, nextPage: Int = 0) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            _state.update { currentState ->
                val newItems = if (isFirstPage) response?.content.orEmpty() else currentState.showtimes + response?.content.orEmpty()
                currentState.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    showtimes = newItems,
                    currentPage = nextPage,
                    isLastPage = response?.isLast ?: true
                )
            }
        } else {
            _state.update {
                it.copy(isLoadingFirstPage = false, isPaginating = false, error = result.exceptionOrNull()?.message)
            }
        }
    }
}