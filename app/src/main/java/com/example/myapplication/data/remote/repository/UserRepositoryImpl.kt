package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.UserApi
import com.example.myapplication.data.remote.dto.ChangePasswordRequest
import com.example.myapplication.data.remote.dto.ChangePasswordResponse
import com.example.myapplication.data.remote.dto.UserDto
import com.example.myapplication.domain.repository.UserRepository
import com.example.myapplication.utils.compressImageFile
import com.example.myapplication.utils.safeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi

) : UserRepository{
    override suspend fun updateProfile(fullName: String, avatarFile: File?): Result<UserDto> {
        return safeApiCall {

            val fullNameBody = fullName
                .toRequestBody("text/plain".toMediaTypeOrNull())

            // Compress file trước khi upload
            val compressedFile = avatarFile?.let {
                compressImageFile(it)
            }

            val avatarPart = compressedFile?.let {
                val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "avatar",
                    it.name,
                    requestFile
                )
            }

            userApi.updateProfile(fullNameBody, avatarPart)
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<ChangePasswordResponse> {
        return safeApiCall {
            userApi.changePassword(ChangePasswordRequest(oldPassword, newPassword))
        }
    }
}