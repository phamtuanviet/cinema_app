package com.example.myapplication.data.remote.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String
)

data class RegisterResponse(
    val message: String,
)

