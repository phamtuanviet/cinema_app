package com.example.myapplication.presentation.screen.auth.verifyforgot

data class VerifyForgotPasswordState(
    val email: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val resetToken: String = ""
)