package com.example.myapplication.presentation.screen.auth.reset

data class ResetPasswordState(

    val password: String = "",
    val confirmPassword: String = "",

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,

    val error: String? = null

)