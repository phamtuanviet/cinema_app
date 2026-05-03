package com.example.myapplication.presentation.screen.profile.my_tickets

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.BookingMyBookingDto
import com.example.myapplication.data.remote.enums.BookingTab
import com.example.myapplication.domain.repository.BookingRepository
import com.example.myapplication.domain.repository.RatingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class ProfileMyTicketsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val ratingRepository: RatingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileMyTicketsState())
    val state: StateFlow<ProfileMyTicketsState> = _state

    init {
        // Lắng nghe dữ liệu từ Room liên tục cho cả 3 tab
        observeBookings("UPCOMING") { bookings -> _state.update { it.copy(upcoming = bookings) } }
        observeBookings("ONGOING") { bookings -> _state.update { it.copy(ongoing = bookings) } }
        observeBookings("COMPLETED") { bookings -> _state.update { it.copy(completed = bookings) } }

        // Load data tab đầu tiên
        loadTab(BookingTab.UPCOMING)
    }

    private fun observeBookings(status: String, updateState: (List<BookingMyBookingDto>) -> Unit) {
        viewModelScope.launch {
            bookingRepository.getMyBookingsFlow(status)
                .catch { e ->
                    // Bắt lỗi tại đây nếu việc convert JSON bị văng lỗi
                }
                .collect { bookings ->
                    updateState(bookings)
                }
        }
    }

    fun selectTab(tab: BookingTab) {
        _state.update { it.copy(selectedTab = tab) }
        loadTab(tab)
    }

    private fun loadTab(tab: BookingTab) {
        // Cập nhật loading state và gọi hàm refresh
        viewModelScope.launch {
            when (tab) {
                BookingTab.UPCOMING -> {
                    _state.update { it.copy(isLoadingUpcoming = true) }
                    bookingRepository.refreshBookingsFromApi("UPCOMING")
                    _state.update { it.copy(isLoadingUpcoming = false) }
                }
                BookingTab.ONGOING -> {
                    _state.update { it.copy(isLoadingOngoing = true) }
                    bookingRepository.refreshBookingsFromApi("ONGOING")
                    _state.update { it.copy(isLoadingOngoing = false) }
                }
                BookingTab.COMPLETED -> {
                    _state.update { it.copy(isLoadingCompleted = true) }
                    bookingRepository.refreshBookingsFromApi("COMPLETED")
                    _state.update { it.copy(isLoadingCompleted = false) }
                }
            }
        }
    }

    private fun loadUpcoming() = viewModelScope.launch {
        _state.update { it.copy(isLoadingUpcoming = true) }
        val res = bookingRepository.getMyBookings("UPCOMING")
        if (res.isSuccess) {
            _state.update { it.copy(upcoming = res.getOrNull() ?: emptyList()) }
        }
        _state.update { it.copy(isLoadingUpcoming = false) }
    }

    private fun loadOngoing() = viewModelScope.launch {
        _state.update { it.copy(isLoadingOngoing = true) }
        val res = bookingRepository.getMyBookings("ONGOING")
        if (res.isSuccess) {
            _state.update { it.copy(ongoing = res.getOrNull() ?: emptyList()) }
        }
        _state.update { it.copy(isLoadingOngoing = false) }
    }

    private fun loadCompleted() = viewModelScope.launch {
        _state.update { it.copy(isLoadingCompleted = true) }
        val res = bookingRepository.getMyBookings("COMPLETED")
        if (res.isSuccess) {
            _state.update { it.copy(completed = res.getOrNull() ?: emptyList()) }
        }
        _state.update { it.copy(isLoadingCompleted = false) }
    }

    fun rateMovie(bookingId: String, movieId: String, score: Int) {
        val current = _state.value
        if (current.ratingLoadingIds.contains(bookingId)) return

        viewModelScope.launch {
            _state.update {
                it.copy(ratingLoadingIds = it.ratingLoadingIds + bookingId)
            }

            val res = ratingRepository.rateMovie(movieId, score)

            if (res.isSuccess) {
                val data = res.getOrNull()

                _state.update { state ->
                    state.copy(
                        completed = state.completed.map {
                            if (it.movie.id == movieId) {
                                it.copy(
                                    userRating = data?.userScore,
                                    averageRating = data?.averageScore
                                )
                            } else it
                        }
                    )
                }
            }

            _state.update {
                it.copy(ratingLoadingIds = it.ratingLoadingIds - bookingId)
            }
        }
    }
}