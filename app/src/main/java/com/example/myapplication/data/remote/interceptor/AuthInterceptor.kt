package com.example.myapplication.data.remote.interceptor

import com.example.myapplication.core.datastore.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = runBlocking {
            sessionManager.getAccessToken()
        }

        val request = chain.request().newBuilder()

        token?.let {
            request.addHeader(
                "Authorization",
                "Bearer $it"
            )
        }

        return chain.proceed(request.build())
    }
}