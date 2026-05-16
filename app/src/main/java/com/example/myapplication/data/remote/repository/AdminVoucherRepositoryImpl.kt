package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.AdminVoucherApi
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminVoucherCreateRequest
import com.example.myapplication.data.remote.dto.AdminVoucherDto
import com.example.myapplication.data.remote.dto.AdminVoucherUpdateRequest
import com.example.myapplication.domain.repository.AdminVoucherRepository
import javax.inject.Inject

class AdminVoucherRepositoryImpl @Inject constructor(
    private val api: AdminVoucherApi
) : AdminVoucherRepository {
    override suspend fun getVouchers(search: String?, status: String, page: Int): Result<AdminPaginatedResponse<AdminVoucherDto>> {
        return try {
            Result.success(api.getVouchers(search, status, page))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVoucherById(id: String) = runCatching { api.getVoucherById(id) }
    override suspend fun updateVoucher(id: String, request: AdminVoucherUpdateRequest) = runCatching { api.updateVoucher(id, request) }
    override suspend fun createVoucher(request: AdminVoucherCreateRequest) = runCatching { api.createVoucher(request) }
}