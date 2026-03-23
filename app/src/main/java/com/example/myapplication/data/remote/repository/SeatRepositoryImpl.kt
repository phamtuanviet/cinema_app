package com.example.myapplication.data.remote.repository

import android.util.Log
import com.example.myapplication.data.remote.api.SeatApi
import com.example.myapplication.data.remote.dto.CancelSeatCoupleRequest
import com.example.myapplication.data.remote.dto.CancelSeatCoupleResponse
import com.example.myapplication.data.remote.dto.CancelSeatRequest
import com.example.myapplication.data.remote.dto.CancelSeatResponse
import com.example.myapplication.data.remote.dto.HoldSeatCoupleRequest
import com.example.myapplication.data.remote.dto.HoldSeatCoupleResponse
import com.example.myapplication.data.remote.dto.HoldSeatRequest
import com.example.myapplication.data.remote.dto.HoldSeatResponse
import com.example.myapplication.data.remote.dto.SeatMapDto
import com.example.myapplication.domain.repository.SeatRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class SeatRepositoryImpl @Inject constructor(
    private val seatApi: SeatApi
) : SeatRepository {
    override suspend fun getSeatMap(showtimeId: String): Result<SeatMapDto> {
        return safeApiCall {
            seatApi.getSeatMap(showtimeId)
        }
    }

    override suspend fun holdSeat(showtimeId: String, seatId: String): Result<HoldSeatResponse> {
        return safeApiCall {
            val result = seatApi.holdSeat(HoldSeatRequest(showtimeId, seatId))
            result
        }
    }

    override suspend fun cancelSeat(showtimeId: String, seatId: String): Result<CancelSeatResponse> {
        return safeApiCall {
            seatApi.cancelSeat(CancelSeatRequest(showtimeId, seatId))
        }

    }

    override suspend fun holdSeatCouple(
        showtimeId: String,
        firstSeatId: String,
        secondSeatId: String
    ): Result<HoldSeatCoupleResponse> {
        return safeApiCall {
            seatApi.holdCoupleSeat(HoldSeatCoupleRequest(showtimeId, firstSeatId, secondSeatId))
        }
    }

    override suspend fun cancelSeatCouple(
        showtimeId: String,
        firstSeatId: String,
        secondSeatId: String
    ): Result<CancelSeatCoupleResponse> {
        return safeApiCall {
            seatApi.cancelCoupleSeat(CancelSeatCoupleRequest(showtimeId, firstSeatId, secondSeatId))
        }
    }
}