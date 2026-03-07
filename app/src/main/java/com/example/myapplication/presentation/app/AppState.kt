package com.example.myapplication.presentation.app

data class AppState(
    val isLoggedIn: Boolean = false,
    val hasOnboarded: Boolean = true,
    val darkTheme: Boolean = false
)