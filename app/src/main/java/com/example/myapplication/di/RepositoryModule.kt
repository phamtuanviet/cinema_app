package com.example.myapplication.di


import com.example.myapplication.data.remote.repository.AuthRepositoryImpl
import com.example.myapplication.data.remote.repository.BannerRepositoryImpl
import com.example.myapplication.data.remote.repository.BookingRepositoryImpl
import com.example.myapplication.data.remote.repository.MovieRepositoryImpl
import com.example.myapplication.data.remote.repository.PaymentRepositoryImpl
import com.example.myapplication.data.remote.repository.SeatHoldSessionRepositoryImpl
import com.example.myapplication.data.remote.repository.SeatRepositoryImpl
import com.example.myapplication.data.remote.repository.ShowtimeRepositoryImpl
import com.example.myapplication.data.remote.repository.VoucherRepositoryImpl
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.BannerRepository
import com.example.myapplication.domain.repository.BookingRepository
import com.example.myapplication.domain.repository.MovieRepository
import com.example.myapplication.domain.repository.PaymentRepository
import com.example.myapplication.domain.repository.SeatHoldSessionRepository
import com.example.myapplication.domain.repository.SeatRepository
import com.example.myapplication.domain.repository.ShowtimeRepository
import com.example.myapplication.domain.repository.VoucherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindMovieRepository(
        impl: MovieRepositoryImpl
    ): MovieRepository

    @Binds
    abstract fun bindBannerRepository(
        impl: BannerRepositoryImpl
    ): BannerRepository

    @Binds
    abstract fun bindShowtimeRepository(
        impl: ShowtimeRepositoryImpl
    ): ShowtimeRepository

    @Binds
    abstract fun bindSeatRepository(
        impl: SeatRepositoryImpl
    ): SeatRepository

    @Binds
    abstract fun bindSeatHoldSessionRepository(
        impl: SeatHoldSessionRepositoryImpl
    ): SeatHoldSessionRepository

    @Binds
    abstract fun bindVoucherRepository(
        impl: VoucherRepositoryImpl
    ): VoucherRepository

    @Binds
    abstract fun bindBookingRepository(
        impl: BookingRepositoryImpl
    ): BookingRepository

    @Binds
    abstract fun bindPaymentRepository(
        impl: PaymentRepositoryImpl
    ): PaymentRepository
}