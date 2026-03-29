package com.example.myapplication.presentation.screen.profile.my_tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.enums.BookingTab
import com.example.myapplication.domain.repository.BookingRepository
import com.example.myapplication.domain.repository.RatingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        loadTab(BookingTab.UPCOMING)
    }

    fun selectTab(tab: BookingTab) {
        _state.update { it.copy(selectedTab = tab) }
        loadTab(tab)
    }

    private fun loadTab(tab: BookingTab) {
        when (tab) {
            BookingTab.UPCOMING -> if (_state.value.upcoming.isEmpty()) loadUpcoming()
            BookingTab.ONGOING -> if (_state.value.ongoing.isEmpty()) loadOngoing()
            BookingTab.COMPLETED -> if (_state.value.completed.isEmpty()) loadCompleted()
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