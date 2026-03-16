package com.example.myapplication.di


import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.data.remote.api.MovieApi
import com.example.myapplication.data.remote.api.SeatApi
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
}