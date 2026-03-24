package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.CinemaApi
import com.example.myapplication.data.remote.dto.CinemaResponse
import com.example.myapplication.data.remote.dto.RegionResponse
import com.example.myapplication.domain.repository.CinemaRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class CinemaRepositoryImpl @Inject constructor(
    private val cinemaApi: CinemaApi
) : CinemaRepository {
    override suspend fun getNearby(
        lat: Double,
        lng: Double,
        radius: Double
    ): Result<List<CinemaResponse>> {
        return safeApiCall {
            cinemaApi.getNearbyCinemas(lat,lng,radius);
        }
    }

    override suspend fun getRegions(): Result<List<RegionResponse>> {
        return safeApiCall {
            cinemaApi.getRegions();
        }
    }

    override suspend fun getCinemaByRegion(
        region: String,
        lat: Double,
        lng: Double
    ): Result<List<CinemaResponse>> {
        return safeApiCall {
            cinemaApi.getCinemaByRegion(region,lat,lng);
        }
    }
}