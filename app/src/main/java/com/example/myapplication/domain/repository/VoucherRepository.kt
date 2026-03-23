package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.VoucherDto

interface VoucherRepository {
    suspend fun addVoucher(code : String) : Result<VoucherDto>
}