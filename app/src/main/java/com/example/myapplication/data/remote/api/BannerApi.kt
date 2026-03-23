package com.example.myapplication.data.remote.api
import com.example.myapplication.data.remote.dto.BannerDto
import retrofit2.http.GET
interface BannerApi {
    @GET("banner/active")
    suspend fun getActiveBanners(): List<BannerDto>
}