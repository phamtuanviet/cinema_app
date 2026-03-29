package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.ChangePasswordResponse
import com.example.myapplication.data.remote.dto.UserDto
import java.io.File

interface UserRepository {

    suspend fun updateProfile(fullName: String, avatarFile: File?): Result<UserDto>

    suspend fun changePassword(oldPassword : String, newPassword :String): Result<ChangePasswordResponse>
}


