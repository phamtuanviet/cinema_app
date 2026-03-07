package com.example.myapplication.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String
)
