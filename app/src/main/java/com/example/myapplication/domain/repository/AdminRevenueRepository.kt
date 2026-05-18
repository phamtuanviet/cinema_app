package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.BannerDto
import com.example.myapplication.data.remote.dto.RevenueSummaryDto

interface AdminRevenueRepository {
    suspend fun getRevenueSummary(timeRange: String, date: String): Result<RevenueSummaryDto>
}