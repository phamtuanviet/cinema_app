package com.example.myapplication.data.remote.repository

import android.util.Log
import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.data.remote.dto.ForgotPasswordRequest
import com.example.myapplication.data.remote.dto.LoginRequest
import com.example.myapplication.data.remote.dto.LogoutRequest
import com.example.myapplication.data.remote.dto.RegisterRequest
import com.example.myapplication.data.remote.dto.ResetPasswordRequest
import com.example.myapplication.data.remote.dto.VerifyEmailRequest
import com.example.myapplication.data.remote.dto.VerifyForgotPasswordRequest
import com.example.myapplication.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Boolean {


        return try {
            val response = authApi.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                    ?: throw Exception("Empty response")

                val user = response.body()!!.user;
                sessionManager.saveAccessToken(response.body()!!.accessToken)
                sessionManager.saveRefreshToken(response.body()!!.refreshToken)
                sessionManager.saveUserId(user.id)
                sessionManager.saveUserEmail(user.email)
                sessionManager.saveRole(user.role)

                return true;
            } else {

                throw Exception("Login failed")
            }
        } catch (e: Exception) {

            Log.e("LOGIN_ERROR", e.toString())
            false
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): Boolean {
        val response = authApi.register(RegisterRequest(email, password, fullName, phone))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")
            return true
        } else {
            throw Exception("Register failed")
        }
    }

    override suspend fun verifyEmail(
        email: String,
        otp: String
    ): Boolean {
        val response = authApi.verifyEmail(VerifyEmailRequest(email, otp))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")


            return true;
        } else {
            throw Exception("Verify failed")
        }
    }

    override suspend fun forgotPassword(
        email: String
    ): Boolean {
        val response = authApi.forgotPassword(ForgotPasswordRequest(email))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")

            return true;
        } else {
            throw Exception("Forgot password failed")
        }
    }

    override suspend fun verifyForgotPassword(
        email: String,
        otp: String
    ): String {
        val response = authApi.verifyForgotPassword(VerifyForgotPasswordRequest(email, otp))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")
            return body.resetToken;
        } else {
            throw Exception("Verify failed")
        }
    }

    override suspend fun resetPassword(
        resetToken: String,
        password: String
    ): Boolean {
        val response = authApi.resetPassword(ResetPasswordRequest(resetToken, password))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")

            return true;
        } else {
            throw Exception("Reset password failed")
        }
    }

    override suspend fun logout(
        refreshToken: String
    ): Boolean {
        val response = authApi.logout(LogoutRequest(refreshToken))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")

            sessionManager.clearToken();
            return true;
        } else {
            throw Exception("Logout failed")
        }
    }
}