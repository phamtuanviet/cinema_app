package com.example.myapplication.di


import com.example.myapplication.data.remote.repository.AdminBannerRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminBookingRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminCinemaRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminComboRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminDashboardRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminGenreRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminMovieRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminNewsRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminRevenueRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminShowtimeRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminUserRepositoryImpl
import com.example.myapplication.data.remote.repository.AdminVoucherRepositoryImpl
import com.example.myapplication.data.remote.repository.AuthRepositoryImpl
import com.example.myapplication.data.remote.repository.BannerRepositoryImpl
import com.example.myapplication.data.remote.repository.BookingRepositoryImpl
import com.example.myapplication.data.remote.repository.ChatRepositoryImpl
import com.example.myapplication.data.remote.repository.CinemaRepositoryImpl
import com.example.myapplication.data.remote.repository.LoyaltyRepositoryImpl
import com.example.myapplication.data.remote.repository.MovieRepositoryImpl
import com.example.myapplication.data.remote.repository.PaymentRepositoryImpl
import com.example.myapplication.data.remote.repository.PostRepositoryImpl
import com.example.myapplication.data.remote.repository.RatingRepositoryImpl
import com.example.myapplication.data.remote.repository.SeatHoldSessionRepositoryImpl
import com.example.myapplication.data.remote.repository.SeatRepositoryImpl
import com.example.myapplication.data.remote.repository.ShowtimeRepositoryImpl
import com.example.myapplication.data.remote.repository.UserRepositoryImpl
import com.example.myapplication.data.remote.repository.VoucherRepositoryImpl
import com.example.myapplication.domain.repository.AdminBannerRepository
import com.example.myapplication.domain.repository.AdminBookingRepository
import com.example.myapplication.domain.repository.AdminCinemaRepository
import com.example.myapplication.domain.repository.AdminComboRepository
import com.example.myapplication.domain.repository.AdminDashboardRepository
import com.example.myapplication.domain.repository.AdminGenreRepository
import com.example.myapplication.domain.repository.AdminMovieRepository
import com.example.myapplication.domain.repository.AdminNewsRepository
import com.example.myapplication.domain.repository.AdminRevenueRepository
import com.example.myapplication.domain.repository.AdminShowtimeRepository
import com.example.myapplication.domain.repository.AdminUserRepository
import com.example.myapplication.domain.repository.AdminVoucherRepository
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.BannerRepository
import com.example.myapplication.domain.repository.BookingRepository
import com.example.myapplication.domain.repository.ChatRepository
import com.example.myapplication.domain.repository.CinemaRepository
import com.example.myapplication.domain.repository.LoyaltyRepository
import com.example.myapplication.domain.repository.MovieRepository
import com.example.myapplication.domain.repository.PaymentRepository
import com.example.myapplication.domain.repository.PostRepository
import com.example.myapplication.domain.repository.RatingRepository
import com.example.myapplication.domain.repository.SeatHoldSessionRepository
import com.example.myapplication.domain.repository.SeatRepository
import com.example.myapplication.domain.repository.ShowtimeRepository
import com.example.myapplication.domain.repository.UserRepository
import com.example.myapplication.domain.repository.VoucherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

    @Binds
    abstract fun bindCinemaRepository(
        impl: CinemaRepositoryImpl
    ): CinemaRepository

    @Binds
    abstract fun bindPostRepository(
        impl: PostRepositoryImpl
    ): PostRepository

    @Binds
    abstract fun bindLoyaltyRepository(
        impl: LoyaltyRepositoryImpl
    ): LoyaltyRepository

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindRatingRepository(
        impl: RatingRepositoryImpl
    ): RatingRepository


    @Binds
    abstract fun bindAdminDashboardRepository(
        impl: AdminDashboardRepositoryImpl
    ): AdminDashboardRepository

    @Binds
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    abstract fun bindAdminMovieRepository(
        impl: AdminMovieRepositoryImpl
    ): AdminMovieRepository

    @Binds
    @Singleton
    abstract fun bindAdminGenreRepository(
        adminGenreRepositoryImpl: AdminGenreRepositoryImpl
    ): AdminGenreRepository

    @Binds
    @Singleton
    abstract fun bindAdminCinemaRepository(
        adminCinemaRepositoryImpl: AdminCinemaRepositoryImpl
    ): AdminCinemaRepository

    @Binds
    @Singleton
    abstract fun bindAdminShowtimeRepository(
        impl: AdminShowtimeRepositoryImpl
    ): AdminShowtimeRepository

    @Binds
    @Singleton
    abstract fun bindAdminUserRepository(
        impl: AdminUserRepositoryImpl
    ): AdminUserRepository

    @Binds
    @Singleton
    abstract fun bindAdminBookingRepository(
        impl: AdminBookingRepositoryImpl
    ): AdminBookingRepository

    @Binds
    @Singleton
    abstract fun bindAdminComboRepository(
        impl: AdminComboRepositoryImpl
    ): AdminComboRepository

    @Binds
    @Singleton
    abstract fun bindAdminVoucherRepository(
        impl: AdminVoucherRepositoryImpl
    ): AdminVoucherRepository

    @Binds
    @Singleton
    abstract fun bindAdminNewsRepository(
        impl: AdminNewsRepositoryImpl
    ): AdminNewsRepository

    @Binds
    @Singleton
    abstract fun bindAdminBannerRepository(
        impl: AdminBannerRepositoryImpl
    ): AdminBannerRepository

    @Binds
    @Singleton
    abstract fun bindAdminRevenueRepository(
        impl: AdminRevenueRepositoryImpl
    ): AdminRevenueRepository
}