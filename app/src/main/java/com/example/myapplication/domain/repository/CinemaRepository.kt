package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.CinemaResponse
import com.example.myapplication.data.remote.dto.RegionResponse

interface CinemaRepository {
    suspend fun getNearby(
        lat: Double,
        lng: Double,
        radius: Double = 100.0
    ): Result<List<CinemaResponse>>

    suspend fun getRegions(): Result<List<RegionResponse>>

    suspend fun getCinemaByRegion(
        region: String,
        lat: Double,
        lng: Double
    ): Result<List<CinemaResponse>>
}