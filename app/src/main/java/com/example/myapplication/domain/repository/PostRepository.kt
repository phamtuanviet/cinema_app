package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.PostDetailResponse
import com.example.myapplication.data.remote.dto.PostResponse

interface PostRepository {
    suspend fun getPosts(type: String, page: Int): Result<List<PostResponse>>

    suspend fun getPostById(id: String): Result<PostDetailResponse>
}