package com.example.myapplication.presentation.screen.profile.ticket_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.BookingRepository
import com.example.myapplication.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileTicketDetailViewModel @Inject constructor(
    private val repository: BookingRepository,
    private val paymentRepository: PaymentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookingId: String = checkNotNull(savedStateHandle["bookingId"])

    private val _state = MutableStateFlow(ProfileTicketDetailState())
    val state: StateFlow<ProfileTicketDetailState> = _state.asStateFlow()

    init {
        loadTicketDetail()
    }

    private fun loadTicketDetail() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        val result = repository.getBookingDetail(bookingId)

        if (result.isSuccess) {
            val bookingData = result.getOrNull()
            if (bookingData != null) {
                _state.update { it.copy(isLoading = false, booking = bookingData) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Dữ liệu vé trống") }
            }
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "Lỗi không xác định"
            _state.update { it.copy(isLoading = false, error = errorMsg) }
        }
    }


    fun requestRefund() = viewModelScope.launch {
        // Bật màn hình chờ của Refund
        _state.update { it.copy(isRefunding = true, error = null) }

        val result = paymentRepository.refundPayment(bookingId)

        if (result.isSuccess) {
            // Hoàn tiền thành công -> Bật cờ success, tắt loading
            _state.update { it.copy(isRefunding = false, refundSuccess = true) }
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "Lỗi hoàn tiền"
            _state.update { it.copy(isRefunding = false, error = errorMsg) }
        }
    }
}