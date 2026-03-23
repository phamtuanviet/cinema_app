package com.example.myapplication.presentation.screen.profile.profile

import com.example.myapplication.data.remote.dto.UserDto

data class ProfileState (
    val isLoading: Boolean = true,
    val error: String? = null,
    val user: UserDto? = null,
    val isLoggedOut : Boolean = false,
    )