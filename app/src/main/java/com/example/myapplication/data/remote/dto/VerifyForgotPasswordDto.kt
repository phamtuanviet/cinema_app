package com.example.myapplication.data.remote.dto

data class VerifyForgotPasswordRequest(
    val email: String,
    val otp: String
)

data class VerifyForgotPasswordResponse(
    val resetToken: String
)
