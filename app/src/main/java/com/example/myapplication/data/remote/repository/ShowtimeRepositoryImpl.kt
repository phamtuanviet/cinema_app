package com.example.myapplication.data.remote.repository

import android.util.Log
import com.example.myapplication.data.remote.api.ShowtimeApi
import com.example.myapplication.data.remote.dto.CinemaShowtimeDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.ShowtimeDetailDto
import com.example.myapplication.domain.repository.ShowtimeRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class ShowtimeRepositoryImpl @Inject constructor(
    private val showTimeApi: ShowtimeApi
) : ShowtimeRepository {

    override suspend fun getShowDates(
        movieId: String
    ): Result<List<String>> {
        return safeApiCall {
            Log.d("ShowtimeRepositoryImpl", "getShowDates: $movieId")

            showTimeApi.getShowDates(movieId)
        }
    }

    override suspend fun getCinemaShowtimes(
        movieId: String,
        date: String,
        lat: Double,
        lng: Double

    ) : Result<List<CinemaShowtimeDto>> {
        return safeApiCall {
            Log.d("ShowtimeRepositoryImpl", "getCinemaShowtimes: $movieId, $date, $lat, $lng")
            showTimeApi.getCinemaShowtimes(movieId, date, lat, lng)
        }
    }

    override suspend fun getMovieByShowtime(showtimeId: String): Result<MovieDto> {
        return safeApiCall {
            showTimeApi.getMovieByShowtime(showtimeId)
        }

    }

    override suspend fun getShowtimeDetail(showtimeId : String) : Result<ShowtimeDetailDto> {
        return safeApiCall {
            showTimeApi.getShowtimeDetail(showtimeId)
        }
    }
}