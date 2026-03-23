package com.example.myapplication.utils

suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {

        when (e) {
            is retrofit2.HttpException -> {
                Result.failure(Exception("Server error: ${e.code()}"))
            }

            is java.io.IOException -> {
                Result.failure(Exception("No internet"))
            }

            else -> {
                Result.failure(e)
            }
        }
    }
}