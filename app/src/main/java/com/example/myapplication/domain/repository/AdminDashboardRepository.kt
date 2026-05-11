package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.DashboardStatsDto
import com.example.myapplication.data.remote.dto.RevenuePointDto

interface AdminDashboardRepository {
    suspend fun getDashboardStats(): Result<DashboardStatsDto>
    suspend fun getRevenueLast7Days(): Result<List<RevenuePointDto>>
}