package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.MovieDto

interface MovieRepository {


    suspend fun getNowShowingMovies(): Result<List<MovieDto>>

    suspend fun getComingSoonMovies(): Result<List<MovieDto>>

    suspend fun getMovie(
        movieId: String
    ): Result<MovieDto>



//    suspend fun confirmBooking(
//        sessionId: UUID,
//        combos: Map<UUID, Int>,
//        voucherId: UUID?,
//        usedPoints: Int
//    )

}