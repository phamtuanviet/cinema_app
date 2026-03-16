package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.HoldSeatRequest
import com.example.myapplication.data.remote.dto.HoldSeatResponse
import com.example.myapplication.data.remote.dto.ReleaseSeatRequestDto
import com.example.myapplication.data.remote.dto.SeatMapDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SeatApi {

    // lấy sơ đồ ghế
    @GET("showtimes/{showtimeId}/seats")
    suspend fun getSeatMap(
        @Path("showtimeId") showtimeId: String
    ): Response<SeatMapDto>


    // giữ ghế
    @POST("seats/hold")
    suspend fun holdSeats(
        @Body request: HoldSeatRequest
    ): Response<HoldSeatResponse>


    // bỏ giữ ghế
    @POST("seats/release")
    suspend fun releaseSeats(
        @Body request: ReleaseSeatRequestDto
    ): Response<Unit>

}