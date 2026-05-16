package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.AdminBookingDetailDto
import com.example.myapplication.data.remote.dto.AdminBookingDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse

interface AdminBookingRepository {
    suspend fun getBookings(search: String?, status: String, page: Int): Result<AdminPaginatedResponse<AdminBookingDto>>

    suspend fun getBookingDetail(id: String): Result<AdminBookingDetailDto>
}