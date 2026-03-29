package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.PostApi
import com.example.myapplication.data.remote.dto.PostDetailResponse
import com.example.myapplication.data.remote.dto.PostResponse
import com.example.myapplication.domain.repository.PostRepository
import com.example.myapplication.utils.safeApiCall
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
) : PostRepository {

    override suspend fun getPosts(
        type: String,
        page: Int
    ): Result<List<PostResponse>> {

        return safeApiCall {
            val response = postApi.getPosts(type, page, 10)
            response.content
        }
    }

    override suspend fun getPostById(id: String): Result<PostDetailResponse> {
        return safeApiCall {
            postApi.getPostById(id)
        }
    }
}