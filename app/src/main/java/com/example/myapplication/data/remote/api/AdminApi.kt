package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.DashboardStatsDto
import com.example.myapplication.data.remote.dto.LoginRequest
import com.example.myapplication.data.remote.dto.LoginResponse
import com.example.myapplication.data.remote.dto.RevenuePointDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdminApi {
    @GET("admin/dashboard/stats")
    suspend fun getDashboardStats(): DashboardStatsDto

    @GET("admin/dashboard/revenue")
    suspend fun getRevenueLast7Days(): List<RevenuePointDto>
}