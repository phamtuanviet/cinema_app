package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminShowtimeCreateRequest
import com.example.myapplication.data.remote.dto.AdminShowtimeDto
import com.example.myapplication.data.remote.dto.AdminShowtimeUpdateRequest
import com.example.myapplication.data.remote.dto.SimpleItemDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminShowtimeApi {
    @GET("admin/showtimes")
    suspend fun getShowtimes(
        @Query("search") search: String?, // Backend sẽ tìm theo Movie Name HOẶC Cinema Name
        @Query("filter") filter: String,  // UPCOMING, ONGOING, PAST
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): AdminPaginatedResponse<AdminShowtimeDto>

    @GET("admin/movies/search")
    suspend fun searchMovies(@Query("query") query: String): List<SimpleItemDto>

    // Tìm rạp theo tên
    @GET("admin/cinemas/search")
    suspend fun searchCinemas(@Query("query") query: String): List<SimpleItemDto>

    // 🔥 API Lấy phòng trống theo Rạp và Thời gian
    @GET("admin/rooms/available")
    suspend fun getAvailableRooms(
        @Query("cinemaId") cinemaId: String,
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): List<SimpleItemDto>

    @POST("admin/showtimes")
    suspend fun createShowtime(@Body request: AdminShowtimeCreateRequest): AdminShowtimeDto

    @GET("admin/showtimes/{id}")
    suspend fun getShowtimeById(@Path("id") id: String): AdminShowtimeDto

    @PUT("admin/showtimes/{id}")
    suspend fun updateShowtime(
        @Path("id") id: String,
        @Body request: AdminShowtimeUpdateRequest
    ): AdminShowtimeDto
}