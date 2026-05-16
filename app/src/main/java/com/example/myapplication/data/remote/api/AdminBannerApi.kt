package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AdminBannerDto
import com.example.myapplication.data.remote.dto.AdminMovieSimpleDto
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

interface AdminBannerApi {
    @GET("admin/banners")
    suspend fun getBanners(
        @Query("search") search: String?, // Dùng để tìm tên phim (nếu tab MOVIE) hoặc URL (nếu tab URL)
        @Query("actionType") actionType: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): AdminPaginatedResponse<AdminBannerDto>

    @GET("admin/banners/{id}")
    suspend fun getBannerById(@Path("id") id: String): AdminBannerDto

    // Gọi nhờ API của Movie để lấy list phim
    @GET("admin/movies/active-list")
    suspend fun getActiveMovies(): List<AdminMovieSimpleDto>

    @Multipart
    @PUT("admin/banners/{id}")
    suspend fun updateBanner(
        @Path("id") id: String,
        @Part image: MultipartBody.Part?,
        @Part("data") data: RequestBody
    ): AdminBannerDto

    @Multipart
    @POST("admin/banners")
    suspend fun createBanner(
        @Part image: MultipartBody.Part?,
        @Part("data") data: RequestBody
    ): AdminBannerDto
}

