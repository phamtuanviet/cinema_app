package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.AdminGenreApi
import com.example.myapplication.data.remote.dto.AdminGenreDto
import com.example.myapplication.domain.repository.AdminGenreRepository
import javax.inject.Inject

class AdminGenreRepositoryImpl @Inject constructor(
    private val api: AdminGenreApi
) : AdminGenreRepository {

    override suspend fun getAllGenres(): Result<List<AdminGenreDto>> {
        return try {
            // Nếu trong project bạn có file base chứa hàm safeApiCall thì hãy dùng nó
            // Ví dụ: return safeApiCall { api.getAllGenres() }

            val response = api.getAllGenres()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}