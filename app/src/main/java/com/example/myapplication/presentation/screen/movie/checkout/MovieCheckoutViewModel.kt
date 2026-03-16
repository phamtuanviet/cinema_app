package com.example.myapplication.presentation.screen.movie.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.enums.PaymentMethod
import com.example.myapplication.domain.repository.MovieRepository
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieCheckoutViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieCheckoutState())
    val state: StateFlow<MovieCheckoutState> = _state

    fun init(bookingId: String) {
        _state.update { it.copy(bookingId = bookingId) }
    }

    fun selectPayment(method: PaymentMethod) {
        _state.update { it.copy(selectedPaymentMethod = method) }
    }

    fun createPayment(
        onPaymentCreated: (String) -> Unit
    ) {

        val current = _state.value

        if (current.selectedPaymentMethod == null) return

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            try {

                val response = repository.createPayment(
                    current.bookingId,
                    current.selectedPaymentMethod!!
                )

                _state.update { it.copy(isLoading = false) }

                onPaymentCreated(response.paymentUrl)

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}