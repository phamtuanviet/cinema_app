package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.BookingMyBookingDto
import com.example.myapplication.data.remote.dto.BookingRequest
import com.example.myapplication.data.remote.dto.BookingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingApi {
    @POST("booking")
    suspend fun createBooking(@Body bookingRequest: BookingRequest): BookingResponse

    @GET("booking/my-bookings")
    suspend fun getMyBookings(
        @Query("type") type: String = "ALL"
    ): List<BookingMyBookingDto>

    @GET("booking/{bookingId}")
    suspend fun getBookingDetail(
        @Path("bookingId") bookingId: String
    ): BookingMyBookingDto

}