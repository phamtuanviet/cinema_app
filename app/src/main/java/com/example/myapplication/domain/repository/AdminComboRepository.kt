package com.example.myapplication.domain.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.data.remote.dto.AdminComboCreateRequest
import com.example.myapplication.data.remote.dto.AdminComboDto
import com.example.myapplication.data.remote.dto.AdminComboUpdateRequest
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse

interface AdminComboRepository {
    suspend fun getCombos(search: String?, isActive: Boolean, page: Int): Result<AdminPaginatedResponse<AdminComboDto>>

    suspend fun getComboById(id: String): Result<AdminComboDto>
    suspend fun updateCombo(id: String, request: AdminComboUpdateRequest, imageUri: Uri?, context: Context): Result<AdminComboDto>

    suspend fun createCombo(request: AdminComboCreateRequest, imageUri: Uri?, context: Context): Result<AdminComboDto>
}