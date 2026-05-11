package com.example.myapplication.data.remote.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.myapplication.data.remote.api.AdminCinemaApi
import com.example.myapplication.data.remote.dto.AdminCinemaCreateRequest
import com.example.myapplication.data.remote.dto.AdminCinemaDto
import com.example.myapplication.data.remote.dto.AdminCinemaUpdateRequest
import com.example.myapplication.data.remote.dto.AdminMovieDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse

import com.example.myapplication.domain.repository.AdminCinemaRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AdminCinemaRepositoryImpl @Inject constructor(
    private val api: AdminCinemaApi
) : AdminCinemaRepository {


    override suspend fun getCinemas(search: String?, page: Int) : Result<AdminPaginatedResponse<AdminCinemaDto>> {
        return try {
            val response = api.getCinemas(
                search = if (search.isNullOrBlank()) null else search,
                page = page
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRegions(): Result<List<String>> =
        try { Result.success(api.getRegions()) } catch (e: Exception) { Result.failure(e) }

    override suspend fun getCineplexes(): Result<List<String>> =
        try { Result.success(api.getCineplexes()) } catch (e: Exception) { Result.failure(e) }

    override suspend fun getCinemaById(id: String): Result<AdminCinemaDto> {
        return try {
            Result.success(api.getCinemaById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCinema(request: AdminCinemaCreateRequest, logoUri: Uri?, context: Context): Result<AdminCinemaDto> {
        return try {
            val jsonBody = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())

            // Chuyển đổi Uri thành MultipartBody.Part (Áp dụng cách tối ưu bạn vừa làm ở phần Movie)
            val logoPart: MultipartBody.Part? = logoUri?.let { uri ->
                val file = getFileFromUri(context, uri) // Hàm tiện ích (Utils) của bạn
                if (file != null) {
                    val mimeType = context.contentResolver.getType(uri) ?: "image/*"
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

                    // Tên param "logo" phải KHỚP với @RequestPart("logo") bên Spring Boot
                    MultipartBody.Part.createFormData("logo", file.name, requestFile)
                } else null
            }

            val response = api.createCinema(logoPart, jsonBody)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCinema(id: String, request: AdminCinemaUpdateRequest, logoUri: Uri?, context: Context): Result<AdminCinemaDto> {
        return try {
            val jsonBody = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())

            // Xử lý ảnh xịn xò như bạn đã viết ở phần Movie
            val logoPart: MultipartBody.Part? = logoUri?.let { uri ->
                val file = getFileFromUri(context, uri) // Hàm Utils của bạn
                if (file != null) {
                    val mimeType = context.contentResolver.getType(uri) ?: "image/*"
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("logo", file.name, requestFile)
                } else null
            }

            val response = api.updateCinema(id, logoPart, jsonBody)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver

            // Cố gắng lấy tên file gốc từ Uri
            var fileName = "temp_poster_${System.currentTimeMillis()}.jpg"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }

            // Tạo một file tạm trong thư mục cache của ứng dụng
            val tempFile = File(context.cacheDir, fileName)
            tempFile.createNewFile()

            // Copy luồng dữ liệu từ Uri sang file tạm
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}