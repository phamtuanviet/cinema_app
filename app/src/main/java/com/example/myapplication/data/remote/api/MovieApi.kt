package com.example.myapplication.data.remote.api


import com.example.myapplication.data.remote.dto.CinemaShowtimeDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.PageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {


    @GET("movie/now-showing")
    suspend fun getNowShowingMovies(
        @Query("search") search: String? = null, // Bắt buộc là "search"
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PageResponse<MovieDto>

    @GET("movie/coming-soon")
    suspend fun getComingSoonMovies(
        @Query("search") search: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PageResponse<MovieDto>

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") movieId: String
    ): MovieDto

    @GET("movies/{movieId}")
    suspend fun getMovie(
        @Path("movieId") movieId: Long
    ): MovieDto


    @GET("movie/search")
    suspend fun searchMovies(
        @Query("query") query: String
    ): List<MovieDto>


    @GET("movies/{movieId}/dates")
    suspend fun getAvailableDates(
        @Path("movieId") movieId: Long
    ): List<String>


    @GET("movies/{movieId}/cinemas")
    suspend fun getCinemas(
        @Path("movieId") movieId: Long,
        @Query("date") date: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): List<CinemaShowtimeDto>




}