package com.example.myapplication.data.remote.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.data.remote.api.AdminComboApi
import com.example.myapplication.data.remote.dto.AdminComboCreateRequest
import com.example.myapplication.data.remote.dto.AdminComboDto
import com.example.myapplication.data.remote.dto.AdminComboUpdateRequest
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.domain.repository.AdminComboRepository
import com.example.myapplication.utils.getFileFromUri
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AdminComboRepositoryImpl @Inject constructor(
    private val api: AdminComboApi
) : AdminComboRepository {
    override suspend fun getCombos(search: String?, isActive: Boolean, page: Int): Result<AdminPaginatedResponse<AdminComboDto>> {
        return try {
            Result.success(api.getCombos(search, isActive, page))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getComboById(id: String) = runCatching { api.getComboById(id) }

    override suspend fun updateCombo(
        id: String,
        request: AdminComboUpdateRequest,
        imageUri: Uri?,
        context: Context
    ): Result<AdminComboDto> {
        return try {
            // Ép cục Request thành chuỗi JSON
            val jsonBody = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())

            val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                val file = getFileFromUri(context, uri) // Nhớ import hàm tiện ích này của bạn nhé
                if (file != null) {
                    val mimeType = context.contentResolver.getType(uri) ?: "image/*"
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

                    // Tên param "image" phải khớp với @RequestPart("image") bên Spring Boot
                    MultipartBody.Part.createFormData("image", file.name, requestFile)
                } else null
            }

            val response = api.updateCombo(id, imagePart, jsonBody)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCombo(
        request: AdminComboCreateRequest,
        imageUri: Uri?,
        context: Context
    ): Result<AdminComboDto> {
        return try {
            val jsonBody = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())

            val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                val file = getFileFromUri(context, uri) // Hàm Utils chuyển Uri thành File của bạn
                if (file != null) {
                    val mimeType = context.contentResolver.getType(uri) ?: "image/*"
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", file.name, requestFile)
                } else null
            }

            val response = api.createCombo(imagePart, jsonBody)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}