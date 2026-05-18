package com.example.myapplication.data.remote.dto

// Cấu trúc dữ liệu cho từng cột trên biểu đồ (VD: label = "Thứ 2", value = 150000)
data class RevenueChartPoint(
    val label: String,
    val value: Double
)

// Dữ liệu tổng quan trả về từ Backend
data class RevenueSummaryDto(
    val totalRevenue: Double,
    val totalSuccessfulTransactions: Int,
    val chartData: List<RevenueChartPoint>
)