package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.BannerDto

interface BannerRepository {

    suspend fun getBanners(): Result<List<BannerDto>>
}