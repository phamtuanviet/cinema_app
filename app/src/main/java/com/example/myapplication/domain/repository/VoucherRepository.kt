package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.UserVoucherResponse
import com.example.myapplication.data.remote.dto.VoucherDto
import com.example.myapplication.data.remote.enums.VoucherStatus

interface VoucherRepository {
    suspend fun addVoucher(code : String) : Result<VoucherDto>

    suspend fun getVouchers(status: VoucherStatus): Result<List<UserVoucherResponse>>


}