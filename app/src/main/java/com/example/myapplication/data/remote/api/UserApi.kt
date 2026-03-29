package com.example.myapplication.data.remote.api


import com.example.myapplication.data.remote.dto.ChangePasswordRequest
import com.example.myapplication.data.remote.dto.ChangePasswordResponse
import com.example.myapplication.data.remote.dto.UserDto
import com.example.myapplication.domain.model.User;
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserApi {
    @GET("users")
    suspend fun getUsers(): List<User>;

    @Multipart
    @PUT("user/me")
    suspend fun updateProfile(
        @Part("fullName") fullName: RequestBody,
        @Part avatar: MultipartBody.Part?
    ): UserDto

    @PUT("user/me/password")
    suspend fun changePassword(
        @Body changePasswordRequest: ChangePasswordRequest
    ) : ChangePasswordResponse
}



