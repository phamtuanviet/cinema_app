package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.RatingResponse

interface RatingRepository {
    suspend fun rateMovie(movieId: String, rating: Int): Result<RatingResponse>

}