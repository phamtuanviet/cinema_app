package com.example.myapplication.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun saveToken(token: String)
    fun getToken(): Flow<String?>
    fun isLoggedIn(): Flow<Boolean>
    suspend fun logout()
}