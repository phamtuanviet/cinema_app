package com.example.myapplication.data.remote.dto

data class ForgotPasswordRequest(
    val email: String,
)

data class ForgotPasswordResponse(
    val message: String,
)
