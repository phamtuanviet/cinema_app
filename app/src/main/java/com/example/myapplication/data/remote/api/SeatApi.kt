package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.CancelSeatCoupleRequest
import com.example.myapplication.data.remote.dto.CancelSeatCoupleResponse
import com.example.myapplication.data.remote.dto.CancelSeatRequest
import com.example.myapplication.data.remote.dto.CancelSeatResponse
import com.example.myapplication.data.remote.dto.HoldSeatCoupleRequest
import com.example.myapplication.data.remote.dto.HoldSeatCoupleResponse
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
    @GET("seat/{showtimeId}/seats")
    suspend fun getSeatMap(
        @Path("showtimeId") showtimeId: String
    ): SeatMapDto


    // giữ ghế
    @POST("seat/hold")
    suspend fun holdSeat(
        @Body request: HoldSeatRequest
    ): HoldSeatResponse


    // bỏ giữ ghế
    @POST("seat/cancel")
    suspend fun cancelSeat(
        @Body request: CancelSeatRequest
    ): CancelSeatResponse

    @POST("seat/hold-couple")
    suspend fun holdCoupleSeat(
        @Body request: HoldSeatCoupleRequest
    ): HoldSeatCoupleResponse

    @POST("seat/cancel-couple")
    suspend fun cancelCoupleSeat(
        @Body request: CancelSeatCoupleRequest
    ): CancelSeatCoupleResponse

}