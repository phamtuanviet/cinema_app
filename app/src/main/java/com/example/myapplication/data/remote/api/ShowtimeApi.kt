package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.CinemaShowtimeDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.ShowtimeDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShowtimeApi {
    @GET("showtime/{movieId}/show-dates")
    suspend fun getShowDates(@Path("movieId") movieId: String): List<String>

    @GET("showtime/cinema-showtimes")
    suspend fun getCinemaShowtimes(
        @Query("movieId") movieId: String,
        @Query("date") date: String,   // format: yyyy-MM-dd
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ) : List<CinemaShowtimeDto>

    @GET("showtime/{showtimeId}/movie")
    suspend fun getMovieByShowtime(
        @Path("showtimeId") showtimeId: String
    ) : MovieDto

    @GET("showtime/{showtimeId}")
    suspend fun getShowtimeDetail(
        @Path("showtimeId") showtimeId : String
    ) : ShowtimeDetailDto



}