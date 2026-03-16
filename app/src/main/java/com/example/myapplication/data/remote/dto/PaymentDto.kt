package com.example.myapplication.data.remote.dto

import com.example.myapplication.data.remote.enums.PaymentMethod

data class PaymentDto(
    val paymentUrl : String
)

data class CreatePaymentRequest(
    val bookingId : String,
    val paymentMethod : PaymentMethod
)

