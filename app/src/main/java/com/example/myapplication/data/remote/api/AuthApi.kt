package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.ForgotPasswordRequest
import com.example.myapplication.data.remote.dto.ForgotPasswordResponse
import com.example.myapplication.data.remote.dto.LoginRequest
import com.example.myapplication.data.remote.dto.LoginResponse
import com.example.myapplication.data.remote.dto.LogoutRequest
import com.example.myapplication.data.remote.dto.LogoutResponse
import com.example.myapplication.data.remote.dto.RefreshRequest
import com.example.myapplication.data.remote.dto.RefreshResponse
import com.example.myapplication.data.remote.dto.RegisterRequest
import com.example.myapplication.data.remote.dto.RegisterResponse
import com.example.myapplication.data.remote.dto.ResetPasswordRequest
import com.example.myapplication.data.remote.dto.ResetPasswordResponse
import com.example.myapplication.data.remote.dto.VerifyEmailRequest
import com.example.myapplication.data.remote.dto.VerifyEmailResponse
import com.example.myapplication.data.remote.dto.VerifyForgotPasswordRequest
import com.example.myapplication.data.remote.dto.VerifyForgotPasswordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshRequest
    ): RefreshResponse

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("auth/verify")
    suspend fun verifyEmail(
        @Body request: VerifyEmailRequest
    ): Response<VerifyEmailResponse>

    @POST("auth/forgot")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @POST("auth/verify-forgot")
    suspend fun verifyForgotPassword(
        @Body request: VerifyForgotPasswordRequest
    ): Response<VerifyForgotPasswordResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<LogoutResponse>

}