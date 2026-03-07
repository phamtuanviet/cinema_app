package com.example.myapplication.presentation.screen.auth.verify


data class VerifyEmailState(
    val email: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)