package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.SeatHoldSessionInfoDto
import retrofit2.http.GET
import retrofit2.http.Path

interface SeatHoldSessionApi {
    @GET("seat-hold-session/{seatHoldSessionId}/info")
    suspend fun getSeatHoldSessionInfo(@Path("seatHoldSessionId") seatHoldSessionId: String): SeatHoldSessionInfoDto
}