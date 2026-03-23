package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.BookingDto
import com.example.myapplication.data.remote.dto.BookingResponse

interface BookingRepository{
    suspend fun booking(seatHoldSessionId :String
                        ,selectedCombos: Map<String, Int>
                        ,voucherId: String?,usedPoints: Int)
    : Result<BookingResponse>
}

