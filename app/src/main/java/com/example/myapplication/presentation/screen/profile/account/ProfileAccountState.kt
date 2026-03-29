package com.example.myapplication.presentation.screen.profile.account

import com.example.myapplication.data.remote.dto.UserDto
import java.io.File

data class ProfileAccountState(
    val isLoading: Boolean = false,

    val user: UserDto? = null,

    val fullName: String = "",
    val avatarFile: File? = null,

    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,

    val error: String? = null
)