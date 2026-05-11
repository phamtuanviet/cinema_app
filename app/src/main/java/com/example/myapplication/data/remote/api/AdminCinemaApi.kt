package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AdminCinemaDto
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

interface AdminCinemaApi {
    @GET("admin/cinemas")
    suspend fun getCinemas(
        @Query("search") search: String?,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): AdminPaginatedResponse<AdminCinemaDto>

    @GET("admin/cinemas/{id}")
    suspend fun getCinemaById(@Path("id") id: String): AdminCinemaDto

    @Multipart
    @POST("admin/cinemas")
    suspend fun createCinema(
        @Part logo: MultipartBody.Part?,
        @Part("data") data: RequestBody
    ): AdminCinemaDto

    @Multipart
    @PUT("admin/cinemas/{id}")
    suspend fun updateCinema(
        @Path("id") id: String,
        @Part logo: MultipartBody.Part?,
        @Part("data") data: RequestBody
    ): AdminCinemaDto

    @GET("admin/cinemas/regions")
    suspend fun getRegions(): List<String>

    @GET("admin/cinemas/cineplexes")
    suspend fun getCineplexes(): List<String>
}