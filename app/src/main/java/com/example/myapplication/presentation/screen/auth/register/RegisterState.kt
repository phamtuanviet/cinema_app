package com.example.myapplication.presentation.screen.auth.register

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)