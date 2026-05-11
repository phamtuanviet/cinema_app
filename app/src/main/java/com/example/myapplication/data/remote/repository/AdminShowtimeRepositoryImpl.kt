package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.AdminShowtimeApi
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminShowtimeCreateRequest
import com.example.myapplication.data.remote.dto.AdminShowtimeDto
import com.example.myapplication.data.remote.dto.AdminShowtimeUpdateRequest
import com.example.myapplication.domain.repository.AdminShowtimeRepository
import javax.inject.Inject

class AdminShowtimeRepositoryImpl @Inject constructor(
    private val api: AdminShowtimeApi
) : AdminShowtimeRepository {
    override suspend fun getShowtimes(search: String?, filter: String, page: Int): Result<AdminPaginatedResponse<AdminShowtimeDto>> {
        return try {
            Result.success(api.getShowtimes(search, filter, page))
        } catch (e: Exception) {
            Result.failure(e)

        }
    }

    override suspend fun searchMovies(q: String) = runCatching { api.searchMovies(q) }
    override suspend fun searchCinemas(q: String) = runCatching { api.searchCinemas(q) }
    override suspend fun getAvailableRooms(cId: String, sTime: String, eTime: String) = runCatching { api.getAvailableRooms(cId, sTime, eTime) }
    override suspend fun createShowtime(req: AdminShowtimeCreateRequest) = runCatching { api.createShowtime(req) }

    override suspend fun getShowtimeById(id: String) = runCatching { api.getShowtimeById(id) }
    override suspend fun updateShowtime(id: String, req: AdminShowtimeUpdateRequest) = runCatching { api.updateShowtime(id, req) }
}
