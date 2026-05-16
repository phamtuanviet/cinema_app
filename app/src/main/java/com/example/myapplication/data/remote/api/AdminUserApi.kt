package com.example.myapplication.data.remote.api


import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminUserDetailDto
import com.example.myapplication.data.remote.dto.AdminUserDto
import com.example.myapplication.data.remote.dto.AdminUserUpdateRequest
import retrofit2.http.Body
import javax.inject.Inject
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminUserApi {
    @GET("admin/users")
    suspend fun getUsers(
        @Query("search") search: String?, // Tìm theo email, tên, số điện thoại
        @Query("role") role: String,      // ADMIN hoặc USER
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): AdminPaginatedResponse<AdminUserDto>

    @PUT("admin/users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: AdminUserUpdateRequest
    ): AdminUserDto

    @GET("admin/users/{id}/details")
    suspend fun getUserDetail(@Path("id") id: String): AdminUserDetailDto
}