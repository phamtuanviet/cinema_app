package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.BookingRequest
import com.example.myapplication.data.remote.dto.BookingResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BookingApi {
    @POST("booking")
    suspend fun createBooking(@Body bookingRequest: BookingRequest): BookingResponse

}