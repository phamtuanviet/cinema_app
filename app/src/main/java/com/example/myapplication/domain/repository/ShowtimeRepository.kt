package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.CinemaShowtimeDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.ShowtimeDetailDto

interface ShowtimeRepository {


    suspend fun getShowDates(
        movieId: String
    ): Result<List<String>>

    suspend fun getCinemaShowtimes(
        movieId: String,
        date: String,
        lat: Double,
        lng: Double
    ): Result<List<CinemaShowtimeDto>>

    suspend fun getMovieByShowtime(
        showtimeId : String
    ) :Result<MovieDto>

    suspend fun getShowtimeDetail(
        showtimeId : String
    ) : Result<ShowtimeDetailDto>



}