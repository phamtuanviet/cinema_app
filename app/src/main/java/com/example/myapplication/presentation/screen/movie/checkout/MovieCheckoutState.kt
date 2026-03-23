package com.example.myapplication.presentation.screen.movie.checkout

import com.example.myapplication.data.remote.enums.PaymentMethod

data class MovieCheckoutState(
    val selectedPaymentMethod: PaymentMethod? = null,
    val isLoading: Boolean = false,
    val paymentUrl: String? = null,
    val error: String? = null
)