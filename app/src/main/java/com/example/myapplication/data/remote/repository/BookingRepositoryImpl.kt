package com.example.myapplication.data.remote.repository

import android.util.Log
import com.example.myapplication.data.local.dao.BookingDao
import com.example.myapplication.data.local.dto.toDto
import com.example.myapplication.data.local.dto.toEntity
import com.example.myapplication.data.remote.api.BookingApi
import com.example.myapplication.data.remote.dto.BookingDto
import com.example.myapplication.data.remote.dto.BookingMyBookingDto
import com.example.myapplication.data.remote.dto.BookingRequest
import com.example.myapplication.data.remote.dto.BookingResponse
import com.example.myapplication.domain.repository.BookingRepository
import com.example.myapplication.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val api: BookingApi,
    private val dao: BookingDao
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

     override fun getMyBookingsFlow(status: String): Flow<List<BookingMyBookingDto>> {
        return dao.getBookingsByStatus(status).map { entities ->
            entities.map { it.toDto() }
        }
    }

     override suspend fun refreshBookingsFromApi(status: String): Result<Unit> {
        return try {
            val response = api.getMyBookings(status)

            Log.d("DEBUG_APP", "Status của vé đầu tiên từ API là: [${response.firstOrNull()?.status}]")
            val entities = response.map { it.toEntity() }
            dao.insertBookings(entities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookingDetail(bookingId: String): Result<BookingMyBookingDto> {
        return try {
            val response = api.getBookingDetail(bookingId)

            dao.insertBookings(listOf(response.toEntity()))

            Result.success(response)
        } catch (e: Exception) {
            try {
                val localData = dao.getBookingById(bookingId)
                if (localData != null) {
                    Result.success(localData.toDto())
                } else {
                    Result.failure(Exception("Không có kết nối mạng và không tìm thấy vé lưu tạm."))
                }
            } catch (localDbError: Exception) {
                Result.failure(e)
            }
        }
    }
}