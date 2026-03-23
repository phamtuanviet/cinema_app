package com.example.myapplication.data.remote.api


import com.example.myapplication.domain.model.User;

import java.util.List;

import retrofit2.http.GET;

interface UserApi {
    @GET("users")
    suspend fun getUsers(): List<User>;
}

