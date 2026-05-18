package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.RevenueSummaryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AdminRevenueApi {
    // timeRange có thể là: "DAY", "WEEK", "MONTH", "YEAR"
    @GET("admin/revenue")
    suspend fun getRevenueSummary(
        @Query("timeRange") timeRange: String,
        @Query("targetDate") targetDate: String
    ): RevenueSummaryDto
}