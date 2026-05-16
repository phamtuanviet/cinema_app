package com.example.myapplication.presentation.screen.admin.booking.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminBookingDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.BookingStatusTab
import com.example.myapplication.domain.repository.AdminBookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminBookingListState(
    val bookings: List<AdminBookingDto> = emptyList(),
    val isLoadingFirstPage: Boolean = true,
    val isPaginating: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val currentTab: BookingStatusTab = BookingStatusTab.PAID, // Thường Admin hay xem vé đã thanh toán nhất

    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

@HiltViewModel
class AdminBookingListViewModel @Inject constructor(
    private val repository: AdminBookingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminBookingListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFirstPage()
    }

    fun onSearchQueryChange(query: String) {
        if (_state.value.searchQuery == query) return

        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500) // Debounce 0.5s
            loadFirstPage()
        }
    }

    fun onTabSelected(tab: BookingStatusTab) {
        if (_state.value.currentTab == tab) return
        _state.update { it.copy(currentTab = tab) }
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoadingFirstPage = true, error = null, currentPage = 0, isLastPage = false, bookings = emptyList())
            }

            val currentState = _state.value
            val result = repository.getBookings(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                status = currentState.currentTab.statusValue,
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
            val result = repository.getBookings(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                status = currentState.currentTab.statusValue,
                page = nextPage
            )

            handleResult(result, isFirstPage = false, nextPage = nextPage)
        }
    }

    private fun handleResult(result: Result<AdminPaginatedResponse<AdminBookingDto>>, isFirstPage: Boolean, nextPage: Int = 0) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            _state.update { currentState ->
                val newItems = if (isFirstPage) response?.content.orEmpty() else currentState.bookings + response?.content.orEmpty()
                currentState.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    bookings = newItems,
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