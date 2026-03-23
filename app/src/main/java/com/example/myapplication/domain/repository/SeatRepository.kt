package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.CancelSeatCoupleResponse
import com.example.myapplication.data.remote.dto.CancelSeatResponse
import com.example.myapplication.data.remote.dto.HoldSeatCoupleResponse
import com.example.myapplication.data.remote.dto.HoldSeatResponse
import com.example.myapplication.data.remote.dto.SeatMapDto

interface SeatRepository {
    suspend fun getSeatMap(showtimeId: String): Result<SeatMapDto>

    suspend fun holdSeat(showtimeId: String, seatId: String): Result<HoldSeatResponse>

    suspend fun cancelSeat(showtimeId: String,seatId: String) : Result<CancelSeatResponse>

    suspend fun holdSeatCouple(showtimeId: String, firstSeatId: String, secondSeatId:String): Result<HoldSeatCoupleResponse>

    suspend fun cancelSeatCouple(showtimeId: String,firstSeatId: String, secondSeatId:String) : Result<CancelSeatCoupleResponse>
}