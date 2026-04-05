package com.example.myapplication.di


import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.data.remote.api.BannerApi
import com.example.myapplication.data.remote.api.BookingApi
import com.example.myapplication.data.remote.api.ChatbotApi
import com.example.myapplication.data.remote.api.CinemaApi
import com.example.myapplication.data.remote.api.LoyaltyApi
import com.example.myapplication.data.remote.api.MovieApi
import com.example.myapplication.data.remote.api.PaymentApi
import com.example.myapplication.data.remote.api.PostApi
import com.example.myapplication.data.remote.api.RatingApi
import com.example.myapplication.data.remote.api.SeatApi
import com.example.myapplication.data.remote.api.SeatHoldSessionApi
import com.example.myapplication.data.remote.api.ShowtimeApi
import com.example.myapplication.data.remote.api.UserApi
import com.example.myapplication.data.remote.api.VoucherApi
import com.example.myapplication.data.remote.retrofit.RetrofitClient
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ----------------------------------------------------
    // 1. ĐỊNH NGHĨA QUALIFIERS (Đánh dấu để Hilt phân biệt)
    // ----------------------------------------------------
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MainRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ChatbotRetrofit

    @Provides
    @Singleton
    @MainRetrofit // Đánh dấu đây là Retrofit của Spring Boot
    fun provideMainRetrofit(
        sessionManager: SessionManager
    ): Retrofit {
        return RetrofitClient.createMain(sessionManager)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    @ChatbotRetrofit // Đánh dấu đây là Retrofit của Express
    fun provideChatbotRetrofit(
        sessionManager: SessionManager
    ): Retrofit {
        return RetrofitClient.createChatbot(sessionManager)
    }

    @Provides
    @Singleton
    fun provideUserApi(
        @MainRetrofit retrofit: Retrofit
    ): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        @MainRetrofit retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieApi(
        @MainRetrofit retrofit: Retrofit
    ): MovieApi {

        return retrofit.create(MovieApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSeatApi(
        @MainRetrofit retrofit: Retrofit
    ): SeatApi {

        return retrofit.create(SeatApi::class.java)

    }

    @Provides
    @Singleton
    fun provideBannerApi(
        @MainRetrofit retrofit: Retrofit
    ): BannerApi {

        return retrofit.create(BannerApi::class.java)

    }

    @Provides
    @Singleton
    fun provideShowtimeApi(
        @MainRetrofit retrofit: Retrofit
    ): ShowtimeApi {

        return retrofit.create(ShowtimeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSeatHoldSessionApi(
        @MainRetrofit retrofit: Retrofit
    ): SeatHoldSessionApi {

        return retrofit.create(SeatHoldSessionApi::class.java)
    }


    @Provides
    @Singleton
    fun provideVoucherApi(
        @MainRetrofit retrofit: Retrofit
    ): VoucherApi {
        return retrofit.create(VoucherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBookingApi(
        @MainRetrofit retrofit: Retrofit
    ): BookingApi {
        return retrofit.create(BookingApi::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApi(
        @MainRetrofit retrofit: Retrofit
    ): PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCinemaApi(
        @MainRetrofit retrofit: Retrofit
    ): CinemaApi {
        return retrofit.create(CinemaApi::class.java)
    }

    @Provides
    @Singleton
    fun providePostApi(
        @MainRetrofit retrofit: Retrofit
    ): PostApi {
        return retrofit.create(PostApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoyaltyApi(
        @MainRetrofit retrofit: Retrofit
    ): LoyaltyApi {
        return retrofit.create(LoyaltyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRatingApi(
        @MainRetrofit retrofit: Retrofit
    ): RatingApi {
        return retrofit.create(RatingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatbotApi(
        @ChatbotRetrofit retrofit: Retrofit
    ): ChatbotApi {
        return retrofit.create(ChatbotApi::class.java)
    }
}