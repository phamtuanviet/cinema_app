package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AdminNewsDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminVoucherSimpleDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminNewsApi {
    @GET("admin/posts")
    suspend fun getNews(
        @Query("search") search: String?, // Backend sẽ tự tìm theo title HOẶC voucher_code
        @Query("type") type: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): AdminPaginatedResponse<AdminNewsDto>

    @GET("admin/posts/{id}")
    suspend fun getPostById(@Path("id") id: String): AdminNewsDto

    // Lấy list voucher để Admin chọn cho bài viết loại VOUCHER
    @GET("admin/vouchers/active-list")
    suspend fun getActiveVouchers(): List<AdminVoucherSimpleDto>

    @Multipart
    @PUT("admin/posts/{id}")
    suspend fun updatePost(
        @Path("id") id: String,
        @Part thumbnail: MultipartBody.Part?,
        @Part("data") data: RequestBody
    ): AdminNewsDto

    @Multipart
    @POST("admin/posts")
    suspend fun createPost(
        @Part thumbnail: MultipartBody.Part?,
        @Part("data") data: RequestBody
    ): AdminNewsDto
}