package com.example.myapplication.di


import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.data.remote.api.BannerApi
import com.example.myapplication.data.remote.api.BookingApi
import com.example.myapplication.data.remote.api.MovieApi
import com.example.myapplication.data.remote.api.PaymentApi
import com.example.myapplication.data.remote.api.SeatApi
import com.example.myapplication.data.remote.api.SeatHoldSessionApi
import com.example.myapplication.data.remote.api.ShowtimeApi
import com.example.myapplication.data.remote.api.UserApi
import com.example.myapplication.data.remote.api.VoucherApi
import com.example.myapplication.data.remote.retrofit.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        sessionManager: SessionManager
    ): Retrofit {
        return RetrofitClient.create(sessionManager)
    }

    @Provides
    @Singleton
    fun provideUserApi(
        retrofit: Retrofit
    ): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieApi(
        retrofit: Retrofit
    ): MovieApi {

        return retrofit.create(MovieApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSeatApi(
        retrofit: Retrofit
    ): SeatApi {

        return retrofit.create(SeatApi::class.java)

    }

    @Provides
    @Singleton
    fun provideBannerApi(
        retrofit: Retrofit
    ): BannerApi {

        return retrofit.create(BannerApi::class.java)

    }

    @Provides
    @Singleton
    fun provideShowtimeApi(
        retrofit: Retrofit
    ): ShowtimeApi {

        return retrofit.create(ShowtimeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSeatHoldSessionApi(
        retrofit: Retrofit
    ): SeatHoldSessionApi {

        return retrofit.create(SeatHoldSessionApi::class.java)
    }


    @Provides
    @Singleton
    fun provideVoucherApi(
        retrofit: Retrofit
    ): VoucherApi {
        return retrofit.create(VoucherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBookingApi(
        retrofit: Retrofit
    ): BookingApi {
        return retrofit.create(BookingApi::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApi(
        retrofit: Retrofit
    ): PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }


}