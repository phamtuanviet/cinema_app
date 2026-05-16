package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminVoucherCreateRequest
import com.example.myapplication.data.remote.dto.AdminVoucherDto
import com.example.myapplication.data.remote.dto.AdminVoucherUpdateRequest

interface AdminVoucherRepository {
    suspend fun getVouchers(search: String?, status: String, page: Int): Result<AdminPaginatedResponse<AdminVoucherDto>>

    suspend fun getVoucherById(id: String): Result<AdminVoucherDto>
    suspend fun updateVoucher(id: String, request: AdminVoucherUpdateRequest): Result<AdminVoucherDto>

    suspend fun createVoucher(request: AdminVoucherCreateRequest): Result<AdminVoucherDto>
}