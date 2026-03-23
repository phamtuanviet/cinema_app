package com.example.myapplication.data.remote.repository

import android.util.Log
import com.example.myapplication.data.remote.api.MovieApi

import com.example.myapplication.data.remote.dto.BookingDto

import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.PaymentDto

import com.example.myapplication.data.remote.enums.PaymentMethod

import com.example.myapplication.domain.repository.MovieRepository
import com.example.myapplication.utils.safeApiCall

import javax.inject.Inject


class MovieRepositoryImpl @Inject constructor(
    private val movieApi: MovieApi,
) : MovieRepository {



    override suspend fun getNowShowingMovies(): Result<List<MovieDto>> {

        return safeApiCall {

            val resutlt = movieApi.getNowShowingMovies()
            Log.d("MovieRepositoryImpl", "getNowShowingMovies: $resutlt")
            resutlt
        }
    }

    override suspend fun getComingSoonMovies(): Result<List<MovieDto>> {
        return safeApiCall {
            movieApi.getComingSoonMovies()
        }
    }

    override suspend fun getMovie(movieId: String): Result<MovieDto> {
        return safeApiCall {
            val movie  = movieApi.getMovieDetail(movieId);
            Log.d("MovieRepositoryImpl", "getMovie: $movie")
            movie
        }
    }
}