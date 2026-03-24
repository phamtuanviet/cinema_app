package com.example.myapplication.data.remote.interceptor

import android.util.Log
import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.data.remote.dto.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val authApi: AuthApi
) : Authenticator {

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {

        val refreshToken = runBlocking {
            sessionManager.getRefreshToken()
        } ?: return null

        return try {

            Log.d("TokenAuthenticator", "Refresh goi lan 2: '$refreshToken'")

            val newToken = runBlocking {
                authApi.refreshToken(
                    RefreshRequest(refreshToken)
                )
            }



            runBlocking {
                sessionManager.saveTokens(
                    newToken.accessToken,
                    newToken.refreshToken
                )
            }

            response.request.newBuilder()
                .header(
                    "Authorization",
                    "Bearer ${newToken.accessToken}"
                )
                .build()

        } catch (e: Exception) {
            Log.d("TokenAuthenticator", "Refresh token: '$refreshToken'")
            Log.d("TokenAuthenticator", e.toString())

            runBlocking { sessionManager.clearTokens() }
            Log.d("TokenAuthenticator", "Refresh token: '$refreshToken'")
            null
        }
    }
}