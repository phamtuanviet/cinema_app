package com.example.myapplication.data.remote.retrofit

import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.data.remote.interceptor.AuthInterceptor
import com.example.myapplication.data.remote.interceptor.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://localhost:8080/api/"

    fun create(sesionManager: SessionManager): Retrofit {

        val tempRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authApi = tempRetrofit.create(AuthApi::class.java)

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sesionManager))
            .authenticator(TokenAuthenticator(sesionManager, authApi))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}