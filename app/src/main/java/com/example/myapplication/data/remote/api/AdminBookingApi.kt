package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AdminBookingDetailDto
import com.example.myapplication.data.remote.dto.AdminBookingDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import javax.inject.Inject
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminBookingApi {
    @GET("admin/bookings")
    suspend fun getBookings(
        @Query("search") search: String?, // Backend sẽ tìm theo email HOẶC ticketCode
        @Query("status") status: String,  // PENDING, PAID, CANCELLED, REFUNDED
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): AdminPaginatedResponse<AdminBookingDto>

    @GET("admin/bookings/{id}")
    suspend fun getBookingDetail(@Path("id") id: String): AdminBookingDetailDto
}