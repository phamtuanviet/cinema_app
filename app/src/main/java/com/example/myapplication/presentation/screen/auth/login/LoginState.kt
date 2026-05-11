package com.example.myapplication.presentation.screen.auth.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val role: String? = null, // 🔥 THÊM TRƯỜNG NÀY ĐỂ LƯU ROLE
    val error: String? = null
)