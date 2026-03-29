package com.example.myapplication.presentation.screen.profile.change_password

data class ProfileChangePasswordState (
    val isLoading : Boolean = false,
    val oldPassword : String = "",
    val newPassword : String = "",
    val isError : Boolean = false,
    val message: String = "",
    val isSuccess : Boolean = false
)

