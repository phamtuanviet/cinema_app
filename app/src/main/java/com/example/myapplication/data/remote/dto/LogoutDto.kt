package com.example.myapplication.data.remote.dto

import android.os.Message

data class LogoutRequest(
    val refreshToken : String
)

data class LogoutResponse(
    val message: String,
)
