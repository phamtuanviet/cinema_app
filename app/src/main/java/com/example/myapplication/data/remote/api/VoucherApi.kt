package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AddVoucherRequest
import com.example.myapplication.data.remote.dto.VoucherDto
import com.example.myapplication.domain.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.List

interface VoucherApi {
    @POST("voucher")
    suspend fun addVoucher(@Body request : AddVoucherRequest): VoucherDto
}
