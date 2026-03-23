package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.PaymentApi
import com.example.myapplication.data.remote.dto.CreatePaymentRequest
import com.example.myapplication.data.remote.dto.PaymentDto
import com.example.myapplication.data.remote.enums.PaymentMethod
import com.example.myapplication.domain.repository.PaymentRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val paymentApi: PaymentApi
) : PaymentRepository
 {
     override suspend fun createPayment(
         bookingId: String,
         method: PaymentMethod
     ): Result<PaymentDto> {
         return safeApiCall {
             paymentApi.createPayment(CreatePaymentRequest(bookingId, method))
         }
     }
}