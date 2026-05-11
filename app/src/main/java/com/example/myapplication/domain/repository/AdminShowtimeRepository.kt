package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminShowtimeCreateRequest
import com.example.myapplication.data.remote.dto.AdminShowtimeDto
import com.example.myapplication.data.remote.dto.AdminShowtimeUpdateRequest
import com.example.myapplication.data.remote.dto.SimpleItemDto

interface AdminShowtimeRepository {
    suspend fun getShowtimes(search: String?, filter: String, page: Int): Result<AdminPaginatedResponse<AdminShowtimeDto>>
    suspend fun searchMovies(query: String): Result<List<SimpleItemDto>>
    suspend fun searchCinemas(query: String): Result<List<SimpleItemDto>>
    suspend fun getAvailableRooms(cinemaId: String, startTime: String, endTime: String): Result<List<SimpleItemDto>>
    suspend fun createShowtime(request: AdminShowtimeCreateRequest): Result<AdminShowtimeDto>

    suspend fun getShowtimeById(id: String): Result<AdminShowtimeDto>

    suspend fun updateShowtime(id: String, request: AdminShowtimeUpdateRequest): Result<AdminShowtimeDto>
}