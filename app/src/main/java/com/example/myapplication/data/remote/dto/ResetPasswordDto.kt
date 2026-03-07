package com.example.myapplication.data.remote.dto


data class ResetPasswordRequest(
    val resetToken: String,
    val password: String
)

data class ResetPasswordResponse(
    val message: String,
)
