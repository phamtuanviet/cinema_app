package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.AdminBookingApi
import com.example.myapplication.data.remote.dto.AdminBookingDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.domain.repository.AdminBookingRepository
import javax.inject.Inject

class AdminBookingRepositoryImpl @Inject constructor(
    private val api: AdminBookingApi
) : AdminBookingRepository {
    override suspend fun getBookings(search: String?, status: String, page: Int): Result<AdminPaginatedResponse<AdminBookingDto>> {
        return try {
            Result.success(api.getBookings(search, status, page))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookingDetail(id: String) = runCatching { api.getBookingDetail(id) }


}