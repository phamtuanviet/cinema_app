package com.example.myapplication.data.remote.repository

import android.util.Log
import com.example.myapplication.data.remote.api.SeatHoldSessionApi
import com.example.myapplication.data.remote.dto.SeatHoldSessionInfoDto
import com.example.myapplication.domain.repository.SeatHoldSessionRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class SeatHoldSessionRepositoryImpl  @Inject constructor(
    private val seatHoldSessionApi: SeatHoldSessionApi
) : SeatHoldSessionRepository {

    override suspend fun getSeatHoldSessionInfo(seatHoldSessionId: String): Result<SeatHoldSessionInfoDto> {
        return safeApiCall {
            Log.d("SeatHoldSessionRepo", seatHoldSessionId)
            seatHoldSessionApi.getSeatHoldSessionInfo(seatHoldSessionId)
        }
    }
}