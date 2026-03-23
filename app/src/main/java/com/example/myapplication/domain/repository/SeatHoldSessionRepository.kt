package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.SeatHoldSessionInfoDto

interface SeatHoldSessionRepository {
    suspend fun getSeatHoldSessionInfo(seatHoldSessionId: String): Result<SeatHoldSessionInfoDto>
}