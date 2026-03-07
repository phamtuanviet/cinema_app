package com.example.myapplication.data.remote.interceptor

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

            val newToken = runBlocking {
                authApi.refreshToken(
                    RefreshRequest(refreshToken)
                )
            }

            runBlocking {
                sessionManager.saveAccessToken(newToken.accessToken)
                sessionManager.saveRefreshToken(newToken.refreshToken)
            }

            response.request.newBuilder()
                .header(
                    "Authorization",
                    "Bearer ${newToken.accessToken}"
                )
                .build()

        } catch (e: Exception) {
            runBlocking { sessionManager.clearToken() }
            null
        }
    }
}