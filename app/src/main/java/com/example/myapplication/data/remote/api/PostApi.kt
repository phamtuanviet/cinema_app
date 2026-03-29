package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.PageResponse
import com.example.myapplication.data.remote.dto.PostDetailResponse
import com.example.myapplication.data.remote.dto.PostResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {

    @GET("post")
    suspend fun getPosts(
        @Query("type") type: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PageResponse<PostResponse>

    @GET("post/{id}")
    suspend fun getPostById(
        @Path("id") id: String
    ): PostDetailResponse
}