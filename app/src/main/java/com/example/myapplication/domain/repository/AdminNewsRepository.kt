package com.example.myapplication.domain.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.data.remote.dto.AdminNewsDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminPostCreateRequest
import com.example.myapplication.data.remote.dto.AdminPostUpdateRequest
import com.example.myapplication.data.remote.dto.AdminVoucherSimpleDto

interface AdminNewsRepository {
    suspend fun getNews(search: String?, type: String, page: Int): Result<AdminPaginatedResponse<AdminNewsDto>>

    suspend fun getPostById(id: String): Result<AdminNewsDto>
    suspend fun getActiveVouchers(): Result<List<AdminVoucherSimpleDto>>
    suspend fun updatePost(id: String, request: AdminPostUpdateRequest, imageUri: Uri?, context: Context): Result<AdminNewsDto>

    suspend fun createPost(request: AdminPostCreateRequest, imageUri: Uri?, context: Context): Result<AdminNewsDto>
}