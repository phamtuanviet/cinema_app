package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.VoucherApi
import com.example.myapplication.data.remote.dto.AddVoucherRequest
import com.example.myapplication.data.remote.dto.VoucherDto
import com.example.myapplication.domain.repository.VoucherRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class VoucherRepositoryImpl @Inject constructor(
    private val voucherApi: VoucherApi
) : VoucherRepository {
    override suspend fun addVoucher(code : String) : Result<VoucherDto> {
        return safeApiCall {
            voucherApi.addVoucher(AddVoucherRequest(code))

        }
    }
}