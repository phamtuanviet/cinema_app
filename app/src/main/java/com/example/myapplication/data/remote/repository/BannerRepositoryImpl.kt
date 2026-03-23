package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.BannerApi
import com.example.myapplication.data.remote.dto.BannerDto
import com.example.myapplication.domain.repository.BannerRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class BannerRepositoryImpl @Inject constructor(
    private val bannerApi: BannerApi
) : BannerRepository {
    override suspend fun getBanners(): Result<List<BannerDto>> {
        return safeApiCall {
            bannerApi.getActiveBanners()
        }
    }
}