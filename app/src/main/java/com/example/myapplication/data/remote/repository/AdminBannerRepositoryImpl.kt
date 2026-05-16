package com.example.myapplication.data.remote.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.data.remote.api.AdminBannerApi
import com.example.myapplication.data.remote.dto.AdminBannerCreateRequest
import com.example.myapplication.data.remote.dto.AdminBannerDto
import com.example.myapplication.data.remote.dto.AdminBannerUpdateRequest
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.domain.repository.AdminBannerRepository
import com.example.myapplication.utils.getFileFromUri
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AdminBannerRepositoryImpl @Inject constructor(
    private val api: AdminBannerApi
) : AdminBannerRepository {
    override suspend fun getBanners(search: String?, actionType: String, page: Int): Result<AdminPaginatedResponse<AdminBannerDto>> {
        return try {
            Result.success(api.getBanners(search, actionType, page))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBannerById(id: String) = runCatching { api.getBannerById(id) }
    override suspend fun getActiveMovies() = runCatching { api.getActiveMovies() }

    override suspend fun updateBanner(id: String, request: AdminBannerUpdateRequest, imageUri: Uri?, context: Context): Result<AdminBannerDto> {
        return try {
            val jsonBody = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
            val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                val file = getFileFromUri(context, uri)
                if (file != null) {
                    val requestFile = file.asRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", file.name, requestFile)
                } else null
            }
            Result.success(api.updateBanner(id, imagePart, jsonBody))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun createBanner(request: AdminBannerCreateRequest, imageUri: Uri, context: Context): Result<AdminBannerDto> {
        return try {
            val jsonBody = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())

            // Bắt buộc parse ảnh
            val file = getFileFromUri(context, imageUri)
                ?: return Result.failure(Exception("Không thể đọc file ảnh"))

            val requestFile = file.asRequestBody(context.contentResolver.getType(imageUri)?.toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            Result.success(api.createBanner(imagePart, jsonBody))
        } catch (e: Exception) { Result.failure(e) }
    }
}