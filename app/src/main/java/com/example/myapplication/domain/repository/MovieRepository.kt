package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.PageResponse

interface MovieRepository {


    suspend fun getNowShowingMovies(search: String? = null, page: Int = 0): Result<PageResponse<MovieDto>>
    suspend fun getComingSoonMovies(search: String? = null, page: Int = 0): Result<PageResponse<MovieDto>>

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