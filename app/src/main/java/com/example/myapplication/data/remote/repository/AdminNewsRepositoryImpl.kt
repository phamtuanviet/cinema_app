package com.example.myapplication.data.remote.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.data.remote.api.AdminNewsApi
import com.example.myapplication.data.remote.dto.AdminNewsDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminPostCreateRequest
import com.example.myapplication.data.remote.dto.AdminPostUpdateRequest
import com.example.myapplication.domain.repository.AdminNewsRepository
import com.example.myapplication.utils.getFileFromUri
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AdminNewsRepositoryImpl @Inject constructor(
    private val api: AdminNewsApi
) : AdminNewsRepository {
    override suspend fun getNews(search: String?, type: String, page: Int): Result<AdminPaginatedResponse<AdminNewsDto>> {
        return try {
            Result.success(api.getNews(search, type, page))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPostById(id: String) = runCatching { api.getPostById(id) }
    override suspend fun getActiveVouchers() = runCatching { api.getActiveVouchers() }

    override suspend fun updatePost(id: String, request: AdminPostUpdateRequest, imageUri: Uri?, context: Context): Result<AdminNewsDto> {
        return try {
            val jsonBody = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
            val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                val file = getFileFromUri(context, uri)
                if (file != null) {
                    val requestFile = file.asRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("thumbnail", file.name, requestFile)
                } else null
            }
            Result.success(api.updatePost(id, imagePart, jsonBody))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun createPost(request: AdminPostCreateRequest, imageUri: Uri?, context: Context): Result<AdminNewsDto> {
        return try {
            val jsonBody = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
            val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                val file = getFileFromUri(context, uri)
                if (file != null) {
                    val requestFile = file.asRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("thumbnail", file.name, requestFile)
                } else null
            }
            Result.success(api.createPost(imagePart, jsonBody))
        } catch (e: Exception) { Result.failure(e) }
    }
}