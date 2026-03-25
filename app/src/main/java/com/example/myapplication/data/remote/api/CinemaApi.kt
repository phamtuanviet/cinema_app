package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.BookingRequest
import com.example.myapplication.data.remote.dto.BookingResponse
import com.example.myapplication.data.remote.dto.CinemaResponse
import com.example.myapplication.data.remote.dto.CinemaShowtimeResponse
import com.example.myapplication.data.remote.dto.RegionResponse
import com.example.myapplication.data.remote.dto.ShowtimeDatesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CinemaApi {
    @GET("cinema/nearby")
    suspend fun getNearbyCinemas(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double = 100.0
    ): List<CinemaResponse>

    // 🔥 2. Regions
    @GET("cinema/regions")
    suspend fun getRegions(): List<RegionResponse>

    // 🔥 3. Cinema theo region + distance
    @GET("cinema/by-region")
    suspend fun getCinemaByRegion(
        @Query("region") region: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): List<CinemaResponse>

    @GET("cinema/{cinemaId}/showtime-dates")
    suspend fun getShowtimeDates(
        @Path("cinemaId") cinemaId: String
    ): ShowtimeDatesResponse


    @GET("cinema/{cinemaId}/showtimes")
    suspend fun getShowtimes(
        @Path("cinemaId") cinemaId: String,
        @Query("date") date: String
    ): CinemaShowtimeResponse

}