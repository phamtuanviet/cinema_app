package com.example.myapplication.data.remote.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.myapplication.data.remote.api.AdminMovieApi
import com.example.myapplication.data.remote.dto.AdminGenreDto
import com.example.myapplication.data.remote.dto.AdminMovieCreateRequest
import com.example.myapplication.data.remote.dto.AdminMovieDto
import com.example.myapplication.data.remote.dto.AdminMovieUpdateRequest
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.domain.repository.AdminMovieRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AdminMovieRepositoryImpl @Inject constructor(
    private val api: AdminMovieApi
) : AdminMovieRepository {

    override suspend fun getMovies(search: String?, page: Int) : Result<AdminPaginatedResponse<AdminMovieDto>> {
        return try {
            val response = api.getMovies(
                search = if (search.isNullOrBlank()) null else search,
                page = page
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    override suspend fun createMovie(
        request: AdminMovieCreateRequest,
        posterUri: Uri?,
        context: Context
    ): Result<AdminMovieDto> {
        return try {
            // 1. Chuyển DTO thành JSON RequestBody
            val gson = Gson()
            val jsonBody = gson.toJson(request).toRequestBody("application/json".toMediaTypeOrNull())

            // 2. Chuyển đổi Uri thành MultipartBody.Part
            val posterPart: MultipartBody.Part? = posterUri?.let { uri ->
                val file = getFileFromUri(context, uri)
                if (file != null) {
                    // Lấy kiểu MIME của file (ví dụ: "image/jpeg", "image/png"), mặc định là "image/*"
                    val mimeType = context.contentResolver.getType(uri) ?: "image/*"

                    // Tạo RequestBody từ file
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

                    // Tạo MultipartBody.Part.
                    // Lưu ý: Tên tham số "poster" (tham số đầu tiên) phải TRÙNG KHỚP với tên field mà Backend mong đợi
                    MultipartBody.Part.createFormData("poster", file.name, requestFile)
                } else {
                    null
                }
            }

            // 3. Gọi API
            val response = api.createMovie(posterPart, jsonBody)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Hàm tiện ích: Đọc dữ liệu từ Uri và lưu ra một file tạm trong cache
     */
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

    override suspend fun getMovieById(id: String): Result<AdminMovieDto> {
        return try {
            Result.success(api.getMovieById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMovie(id: String, request: AdminMovieUpdateRequest, posterUri: Uri?, context: Context): Result<AdminMovieDto> {
        return try {
            val jsonBody = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())

            val posterPart: MultipartBody.Part? = posterUri?.let { uri ->
                val file = getFileFromUri(context, uri)
                if (file != null) {
                    // Lấy kiểu MIME của file (ví dụ: "image/jpeg", "image/png"), mặc định là "image/*"
                    val mimeType = context.contentResolver.getType(uri) ?: "image/*"

                    // Tạo RequestBody từ file
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

                    // Tạo MultipartBody.Part.
                    // Lưu ý: Tên tham số "poster" (tham số đầu tiên) phải TRÙNG KHỚP với tên field mà Backend mong đợi
                    MultipartBody.Part.createFormData("poster", file.name, requestFile)
                } else {
                    null
                }
            }

            val response = api.updateMovie(id, posterPart, jsonBody)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}