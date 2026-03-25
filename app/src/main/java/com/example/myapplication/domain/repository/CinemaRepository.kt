package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.CinemaResponse
import com.example.myapplication.data.remote.dto.CinemaShowtimeResponse
import com.example.myapplication.data.remote.dto.RegionResponse
import com.example.myapplication.data.remote.dto.ShowtimeDatesResponse

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

    suspend fun getShowtimeDates(cinemaId: String): Result<ShowtimeDatesResponse>

    suspend fun getShowtimes (cinemaId: String, date: String) : Result<CinemaShowtimeResponse>

}