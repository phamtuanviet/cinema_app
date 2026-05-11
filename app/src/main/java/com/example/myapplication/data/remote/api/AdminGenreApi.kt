package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AdminGenreDto
import retrofit2.http.GET


// 2. Định nghĩa API
interface AdminGenreApi {
    @GET("admin/genres")
    suspend fun getAllGenres(): List<AdminGenreDto>
}