package com.example.myapplication.presentation.screen.admin.booking.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminBookingDetailDto
import com.example.myapplication.domain.repository.AdminBookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminBookingDetailState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val detail: AdminBookingDetailDto? = null
)

@HiltViewModel
class AdminBookingDetailViewModel @Inject constructor(
    private val repository: AdminBookingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookingId: String = checkNotNull(savedStateHandle["bookingId"])

    private val _state = MutableStateFlow(AdminBookingDetailState())
    val state = _state.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = repository.getBookingDetail(bookingId)
            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, detail = result.getOrNull()) }
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}