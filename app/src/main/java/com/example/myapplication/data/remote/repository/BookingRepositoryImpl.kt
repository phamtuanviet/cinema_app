package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.BookingApi
import com.example.myapplication.data.remote.dto.BookingDto
import com.example.myapplication.data.remote.dto.BookingMyBookingDto
import com.example.myapplication.data.remote.dto.BookingRequest
import com.example.myapplication.data.remote.dto.BookingResponse
import com.example.myapplication.domain.repository.BookingRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val api: BookingApi
) : BookingRepository {

    override suspend fun booking(
        seatHoldSessionId: String,
        selectedCombos: Map<String, Int>,
        voucherId: String?,
        usedPoints: Int
    ): Result<BookingResponse> {

        return safeApiCall {
            api.createBooking(
                BookingRequest(
                    seatHoldSessionId,
                    selectedCombos,
                    voucherId,
                    usedPoints
                )
            )

        }
    }

    override suspend fun getMyBookings(type: String): Result<List<BookingMyBookingDto>> {
        return safeApiCall {
            api.getMyBookings(type)
        }
    }
}