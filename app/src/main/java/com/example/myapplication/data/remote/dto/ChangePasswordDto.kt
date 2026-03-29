package com.example.myapplication.data.remote.dto

data class ChangePasswordRequest (
    val oldPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse (
    val message: String
)