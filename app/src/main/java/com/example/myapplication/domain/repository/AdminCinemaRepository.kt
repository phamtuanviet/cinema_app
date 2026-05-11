package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.AdminCinemaDto
import com.example.myapplication.data.remote.dto.AdminCinemaUpdateRequest
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import android.content.Context
import android.net.Uri
import com.example.myapplication.data.remote.dto.AdminCinemaCreateRequest

interface AdminCinemaRepository {

    suspend fun getCinemas(search: String?, page: Int) : Result<AdminPaginatedResponse<AdminCinemaDto>>
    suspend fun getRegions(): Result<List<String>>
    suspend fun getCineplexes(): Result<List<String>>

    suspend fun getCinemaById(id: String): Result<AdminCinemaDto>

    suspend fun updateCinema(id: String, request: AdminCinemaUpdateRequest, logoUri: Uri?, context: Context): Result<AdminCinemaDto>

    suspend fun createCinema(request: AdminCinemaCreateRequest, logoUri: Uri?, context: Context): Result<AdminCinemaDto>
}