package com.example.myapplication.presentation.screen.movie.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.enums.PaymentMethod
import com.example.myapplication.domain.repository.PaymentRepository
import com.example.myapplication.presentation.app.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieCheckoutViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieCheckoutState())
    val state: StateFlow<MovieCheckoutState> = _state

    fun selectPaymentMethod(method: PaymentMethod) {
        _state.update {
            it.copy(selectedPaymentMethod = method)
        }
    }

    fun createPayment(bookingId: String) {
        val method = _state.value.selectedPaymentMethod ?: return

        if (method == PaymentMethod.VNPAY) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                paymentRepository.createPayment(bookingId, method)
                    .onSuccess { url ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                paymentUrl = url.paymentUrl
                            )
                        }
                    }
                    .onFailure {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Create payment failed"
                            )
                        }
                    }
            }
        }
    }

    fun clearPaymentUrl() {
        _state.update { it.copy(paymentUrl = null) }
    }
}