package com.example.myapplication.data.remote.retrofit

import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.data.remote.interceptor.AuthInterceptor
import com.example.myapplication.data.remote.interceptor.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val MAIN_BASE_URL = "http://localhost:8080/api/"
    private const val CHATBOT_BASE_URL = "http://localhost:3000/api/"

    // 1. Tách hàm tạo OkHttpClient ra riêng để dùng chung
    private fun getOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        // AuthApi luôn luôn phải gọi về Main Backend (Spring Boot) để làm mới token
        val tempRetrofit = Retrofit.Builder()
            .baseUrl(MAIN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authApi = tempRetrofit.create(AuthApi::class.java)

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .authenticator(TokenAuthenticator(sessionManager, authApi))
            .build()
    }

    // 2. Tạo Retrofit cho Spring Boot
    fun createMain(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(MAIN_BASE_URL)
            .client(getOkHttpClient(sessionManager)) // Tự động gắn token
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 3. Tạo Retrofit cho Chatbot (Express)
    fun createChatbot(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CHATBOT_BASE_URL)
            .client(getOkHttpClient(sessionManager)) // Vẫn dùng chung OkHttp để tự động gắn Token cho Chatbot
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}