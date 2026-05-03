package com.example.myapplication.domain.repository

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
        fcmToken: String?
    ): Boolean

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): Boolean

    suspend fun verifyEmail(
        email: String,
        otp: String
    ): Boolean

    suspend fun forgotPassword(
        email: String
    ): Boolean

    suspend fun verifyForgotPassword(
        email: String,
        otp: String
    ): String

    suspend fun resetPassword(
        resetToken: String,
        password: String
    ): Boolean

    suspend fun logout(
        refreshToken: String
    ): Boolean

}
