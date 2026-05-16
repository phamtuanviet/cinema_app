package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminUserDetailDto
import com.example.myapplication.data.remote.dto.AdminUserDto
import com.example.myapplication.data.remote.dto.AdminUserUpdateRequest

interface AdminUserRepository {
    suspend fun getUsers(search: String?, role: String, page: Int): Result<AdminPaginatedResponse<AdminUserDto>>

    suspend fun updateUser(id: String, req: AdminUserUpdateRequest) : Result<AdminUserDto>

    suspend fun getUserDetail(id: String): Result<AdminUserDetailDto>
}