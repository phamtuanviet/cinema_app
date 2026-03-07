package com.example.myapplication.data.remote.dto

data class UserDto(
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val isVerified: Boolean,
    val role: String
)