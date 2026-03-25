package com.example.myapplication.presentation.screen.booking.checkout

import com.example.myapplication.data.remote.enums.PaymentMethod

data class BookingCheckoutState(
    val selectedPaymentMethod: PaymentMethod? = null,
    val isLoading: Boolean = false,
    val paymentUrl: String? = null,
    val error: String? = null
)