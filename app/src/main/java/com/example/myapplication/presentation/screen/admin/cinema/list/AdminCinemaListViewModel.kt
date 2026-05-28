package com.example.myapplication.presentation.screen.admin.cinema.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminCinemaDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.domain.repository.AdminCinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminCinemaListState(
    val cinemas: List<AdminCinemaDto> = emptyList(),
    val isLoadingFirstPage: Boolean = true,
    val isPaginating: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

@HiltViewModel
class AdminCinemaListViewModel @Inject constructor(
    private val repository: AdminCinemaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminCinemaListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

//    init {
//        loadFirstPage()
//    }

    fun prepareForReturn() {
        _state.update {
            it.copy(
                cinemas = emptyList(),
                isLoadingFirstPage = true, // Bật sẵn loading
                error = null
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        if (_state.value.searchQuery == query) return

        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        if (query.isEmpty()) {
            loadFirstPage()
        } else {
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
                    cinemas = emptyList()
                )
            }

            val result = repository.getCinemas(
                search = _state.value.searchQuery,
                page = 0
            )

            handleResult(result, isFirstPage = true)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isLoadingFirstPage || currentState.isPaginating || currentState.isLastPage) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isPaginating = true, error = null) }

            val nextPage = currentState.currentPage + 1
            val result = repository.getCinemas(
                search = currentState.searchQuery,
                page = nextPage
            )

            handleResult(result, isFirstPage = false, nextPage = nextPage)
        }
    }

    private fun handleResult(
        result: Result<AdminPaginatedResponse<AdminCinemaDto>>,
        isFirstPage: Boolean,
        nextPage: Int = 0
    ) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            _state.update { currentState ->
                val newCinemas = if (isFirstPage) {
                    response?.content.orEmpty()
                } else {
                    currentState.cinemas + response?.content.orEmpty()
                }

                currentState.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    cinemas = newCinemas,
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