package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.AdminRevenueApi
import com.example.myapplication.data.remote.dto.RevenueSummaryDto
import com.example.myapplication.domain.repository.AdminRevenueRepository
import javax.inject.Inject

class AdminRevenueRepositoryImpl @Inject constructor(
    private val api: AdminRevenueApi
) : AdminRevenueRepository {

    override suspend fun getRevenueSummary(timeRange: String,date: String): Result<RevenueSummaryDto> {
        return try {
            val response = api.getRevenueSummary(timeRange,date)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}