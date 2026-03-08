package com.example.myapplication.presentation.app

data class AppState(
    val isLoggedIn: Boolean = false,
    val hasOnboarded: Boolean = false,
    val darkTheme: Boolean = false,
    val isLoading: Boolean = true,
)