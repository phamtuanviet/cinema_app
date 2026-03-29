package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.RatingApi
import com.example.myapplication.data.remote.dto.RatingRequest
import com.example.myapplication.data.remote.dto.RatingResponse
import com.example.myapplication.domain.repository.RatingRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class RatingRepositoryImpl @Inject constructor(
    private val api: RatingApi

) : RatingRepository {
    override suspend fun rateMovie(movieId: String, rating: Int): Result<RatingResponse> {
        return safeApiCall {
            api.rateMovie(RatingRequest(movieId, rating))
        }
    }
}