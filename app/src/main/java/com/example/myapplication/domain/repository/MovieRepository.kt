package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.CinemaShowtimeDto
import com.example.myapplication.data.remote.dto.MovieDetailDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.domain.model.Movie
import com.example.myapplication.domain.model.MovieDetail

interface MovieRepository {

    suspend fun getBanners(): List<String>

    suspend fun getNowShowingMovies(): List<Movie>

    suspend fun getComingSoonMovies(): List<Movie>

    suspend fun getMovieDetail(
        movieId: String
    ): MovieDetailDto

    suspend fun getMovie(
        movieId: String
    ): MovieDto

    suspend fun getAvailableDates(
        movieId: String
    ): List<String>

    suspend fun getCinemas(
        movieId: String,
        date: String,
        lat: Double?,
        lng: Double?
    ): List<CinemaShowtimeDto>

}