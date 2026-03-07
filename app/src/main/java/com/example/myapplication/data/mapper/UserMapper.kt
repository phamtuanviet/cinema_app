package com.example.myapplication.data.mapper

import com.example.myapplication.data.remote.dto.LoginResponse
import com.example.myapplication.data.remote.dto.UserDto
import com.example.myapplication.domain.model.User

fun UserDto.toUser(): User {
    return User(
        id = id,
        email = email,
        fullName = fullName,
        phone = phone,
        isVerified = isVerified,
        role = role
    )
}