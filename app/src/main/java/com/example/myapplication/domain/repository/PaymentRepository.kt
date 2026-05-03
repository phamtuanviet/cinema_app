package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.PaymentDto
import com.example.myapplication.data.remote.enums.PaymentMethod

interface PaymentRepository {
    suspend fun createPayment(bookingId: String, method: PaymentMethod): Result<PaymentDto>

    suspend fun refundPayment(bookingId: String): Result<Unit>
}