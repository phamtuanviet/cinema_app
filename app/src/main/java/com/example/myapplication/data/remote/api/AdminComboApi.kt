package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AdminComboDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminComboApi {
    @GET("admin/combos")
    suspend fun getCombos(
        @Query("search") search: String?,
        @Query("isActive") isActive: Boolean,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): AdminPaginatedResponse<AdminComboDto>

    @GET("admin/combos/{id}")
    suspend fun getComboById(@Path("id") id: String): AdminComboDto

    @Multipart
    @PUT("admin/combos/{id}")
    suspend fun updateCombo(
        @Path("id") id: String,
        @Part image: MultipartBody.Part?, // Đổi tên part thành "image" cho hợp lý
        @Part("data") data: RequestBody
    ): AdminComboDto

    @Multipart
    @POST("admin/combos")
    suspend fun createCombo(
        @Part image: MultipartBody.Part?,
        @Part("data") data: RequestBody
    ): AdminComboDto
}