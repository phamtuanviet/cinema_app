package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AddVoucherRequest
import com.example.myapplication.data.remote.dto.UserVoucherResponse
import com.example.myapplication.data.remote.dto.VoucherDto
import com.example.myapplication.data.remote.enums.VoucherStatus
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VoucherApi {


    @GET("user-voucher")
    suspend fun getUserVouchers(
        @Query("status") status: VoucherStatus
    ): List<UserVoucherResponse>

    @POST("user-voucher")
    suspend fun addVoucher(
        @Query("code") code: String
    ): VoucherDto
}
