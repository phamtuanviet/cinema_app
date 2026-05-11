package com.example.myapplication.presentation.screen.admin.dashboard

import com.example.myapplication.data.remote.dto.DashboardStatsDto
import com.example.myapplication.data.remote.dto.RevenuePointDto

data class AdminDashboardState(
    val isLoading: Boolean = true,
    val stats: DashboardStatsDto? = null,
    val revenueData: List<RevenuePointDto> = emptyList(),
    val error: String? = null
)