package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.BookingDto
import com.example.myapplication.data.remote.dto.CinemaShowtimeDto
import com.example.myapplication.data.remote.dto.ComboDto
import com.example.myapplication.data.remote.dto.HoldSeatResponse
import com.example.myapplication.data.remote.dto.MovieDetailDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.SeatMapDto
import com.example.myapplication.data.remote.dto.VoucherDto
import com.example.myapplication.domain.model.Movie
import com.example.myapplication.data.remote.dto.CreateBookingRequest
import com.example.myapplication.data.remote.dto.PaymentDto
import com.example.myapplication.data.remote.enums.PaymentMethod

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

    suspend fun getSeatMap(
        showtimeId: String
    ): SeatMapDto


    suspend fun holdSeat(
        showtimeId: String,
        seatId: String
    ): HoldSeatResponse

    suspend fun cancelSeat(seatId: String)

    suspend fun getCombos(): List<ComboDto>

    suspend fun getAvailableVouchers(): List<VoucherDto>

    suspend fun getAvailablePoints(): Int

    suspend fun createBooking(request: CreateBookingRequest): BookingDto

    suspend fun createPayment(bookingId: String,paymentMethod: PaymentMethod): PaymentDto



//    suspend fun confirmBooking(
//        sessionId: UUID,
//        combos: Map<UUID, Int>,
//        voucherId: UUID?,
//        usedPoints: Int
//    )

}