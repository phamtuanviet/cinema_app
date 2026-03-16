package com.example.myapplication.presentation.screen.movie.checkout

import com.example.myapplication.data.remote.enums.PaymentMethod

data class MovieCheckoutState(
    val bookingId: String = "",
    val selectedPaymentMethod: PaymentMethod? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
