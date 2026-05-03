package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.BookingDto
import com.example.myapplication.data.remote.dto.BookingMyBookingDto
import com.example.myapplication.data.remote.dto.BookingResponse
import kotlinx.coroutines.flow.Flow

interface BookingRepository{
    suspend fun booking(seatHoldSessionId :String
                        ,selectedCombos: Map<String, Int>
                        ,voucherId: String?,usedPoints: Int)
    : Result<BookingResponse>

    suspend fun getMyBookings(type: String): Result<List<BookingMyBookingDto>>

    suspend fun refreshBookingsFromApi(status: String): Result<Unit>

    fun getMyBookingsFlow(status: String): Flow<List<BookingMyBookingDto>>

    suspend fun getBookingDetail(bookingId: String): Result<BookingMyBookingDto>
}

