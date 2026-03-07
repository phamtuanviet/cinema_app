package com.example.myapplication.data.remote.dto

data class VerifyEmailRequest(
    val email: String,
    val otp: String
)

data class VerifyEmailResponse(
    val message: String,
)
