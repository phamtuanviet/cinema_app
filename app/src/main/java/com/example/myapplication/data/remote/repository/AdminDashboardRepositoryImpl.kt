package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.AdminApi
import com.example.myapplication.data.remote.dto.BookingRequest
import com.example.myapplication.data.remote.dto.DashboardStatsDto
import com.example.myapplication.data.remote.dto.RevenuePointDto
import com.example.myapplication.domain.repository.AdminDashboardRepository
import com.example.myapplication.utils.safeApiCall
import kotlinx.coroutines.delay
import javax.inject.Inject

class AdminDashboardRepositoryImpl @Inject constructor(
     private val api: AdminApi
) : AdminDashboardRepository {

    override suspend fun getDashboardStats(): Result<DashboardStatsDto> {
        return safeApiCall {
            api.getDashboardStats()
        }
    }

    override suspend fun getRevenueLast7Days(): Result<List<RevenuePointDto>> {
        return safeApiCall {
            api.getRevenueLast7Days()
        }
    }
}