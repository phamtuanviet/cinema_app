package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminVoucherCreateRequest
import com.example.myapplication.data.remote.dto.AdminVoucherDto
import com.example.myapplication.data.remote.dto.AdminVoucherUpdateRequest
import retrofit2.http.Body
import javax.inject.Inject
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminVoucherApi {
    @GET("admin/vouchers")
    suspend fun getVouchers(
        @Query("search") search: String?, // Tìm theo mã code
        @Query("status") status: String,  // Gửi "VALID" hoặc "INVALID" cho backend xử lý
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): AdminPaginatedResponse<AdminVoucherDto>

    @GET("admin/vouchers/{id}")
    suspend fun getVoucherById(@Path("id") id: String): AdminVoucherDto

    @PUT("admin/vouchers/{id}")
    suspend fun updateVoucher(
        @Path("id") id: String,
        @Body request: AdminVoucherUpdateRequest
    ): AdminVoucherDto

    @POST("admin/vouchers")
    suspend fun createVoucher(@Body request: AdminVoucherCreateRequest): AdminVoucherDto
}