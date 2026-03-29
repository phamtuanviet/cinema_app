package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.RatingRequest
import com.example.myapplication.data.remote.dto.RatingResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RatingApi {
    @POST("rating")
    suspend fun rateMovie(
        @Body request: RatingRequest
    ): RatingResponse
}