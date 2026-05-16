package com.example.myapplication.domain.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.data.remote.dto.AdminBannerCreateRequest
import com.example.myapplication.data.remote.dto.AdminBannerDto
import com.example.myapplication.data.remote.dto.AdminBannerUpdateRequest
import com.example.myapplication.data.remote.dto.AdminMovieSimpleDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse

interface AdminBannerRepository {
    suspend fun getBanners(search: String?, actionType: String, page: Int): Result<AdminPaginatedResponse<AdminBannerDto>>

    suspend fun getBannerById(id: String): Result<AdminBannerDto>
    suspend fun getActiveMovies(): Result<List<AdminMovieSimpleDto>>
    suspend fun updateBanner(id: String, request: AdminBannerUpdateRequest, imageUri: Uri?, context: Context): Result<AdminBannerDto>

    suspend fun createBanner(request: AdminBannerCreateRequest, imageUri: Uri, context: Context): Result<AdminBannerDto>
}