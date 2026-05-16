package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.AdminUserApi
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminUserDetailDto
import com.example.myapplication.data.remote.dto.AdminUserDto
import com.example.myapplication.data.remote.dto.AdminUserUpdateRequest
import com.example.myapplication.domain.repository.AdminUserRepository
import javax.inject.Inject

class AdminUserRepositoryImpl @Inject constructor(
    private val api: AdminUserApi
) : AdminUserRepository {
    override suspend fun getUsers(search: String?, role: String, page: Int): Result<AdminPaginatedResponse<AdminUserDto>> {
        return try {
            Result.success(api.getUsers(search, role, page))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun updateUser(id: String, req: AdminUserUpdateRequest) = runCatching { api.updateUser(id, req) }

    override suspend fun getUserDetail(id: String): Result<AdminUserDetailDto> {
        return try {
            Result.success(api.getUserDetail(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
