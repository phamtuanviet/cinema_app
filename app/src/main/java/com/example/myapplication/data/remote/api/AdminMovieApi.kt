package com.example.myapplication.data.remote.api


import com.example.myapplication.data.remote.dto.AdminMovieDto
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

interface AdminMovieApi {
    @GET("admin/movies")
    suspend fun getMovies(
        @Query("search") search: String?,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): AdminPaginatedResponse<AdminMovieDto>

    @Multipart
    @POST("admin/movies")
    suspend fun createMovie(
        @Part poster: MultipartBody.Part?, // File ảnh
        @Part("data") data: RequestBody    // Chuỗi JSON của AdminMovieCreateRequest
    ): AdminMovieDto

    @GET("admin/movies/{id}")
    suspend fun getMovieById(@Path("id") id: String): AdminMovieDto

    @Multipart
    @PUT("admin/movies/{id}")
    suspend fun updateMovie(
        @Path("id") id: String,
        @Part poster: MultipartBody.Part?, // File ảnh mới (nếu có)
        @Part("data") data: RequestBody    // Chuỗi JSON
    ): AdminMovieDto
}