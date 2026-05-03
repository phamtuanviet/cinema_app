package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.CreatePaymentRequest
import com.example.myapplication.data.remote.dto.HoldSeatRequest
import com.example.myapplication.data.remote.dto.HoldSeatResponse
import com.example.myapplication.data.remote.dto.PaymentDto
import com.example.myapplication.data.remote.dto.ReleaseSeatRequestDto
import com.example.myapplication.data.remote.dto.SeatMapDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApi {

    // lấy sơ đồ ghế
    @POST("payment")
    suspend fun createPayment(
        @Body request: CreatePaymentRequest
    ): PaymentDto


    @POST("payment/refund/{bookingId}")
    suspend fun refundPayment(
        @Path("bookingId") bookingId: String
    ): Any
}