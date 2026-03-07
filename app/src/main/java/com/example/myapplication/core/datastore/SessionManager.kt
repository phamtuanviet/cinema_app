package com.example.myapplication.core.datastore

import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: AppDataStore
) {

    suspend fun getAccessToken(): String? {
        return dataStore.accessTokenFlow.first()
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.refreshTokenFlow.first()
    }

    suspend fun getUserId(): String? {
        return dataStore.userIdFlow.first()
    }

    suspend fun getUserEmail(): String? {
        return dataStore.userEmailFlow.first()
    }

    suspend fun getRole(): String? {
        return dataStore.roleFlow.first()
    }

    suspend fun getDarkTheme(): Boolean {
        return dataStore.darkThemeFlow.first()
    }

    suspend fun saveAccessToken(accessToken: String) {
        dataStore.saveAccessToken(accessToken)
    }

   suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.saveRefreshToken(refreshToken)
    }


    suspend fun clearToken() {
        dataStore.clearToken()
    }

    suspend fun saveUserId(id: String) {
        dataStore.saveUserId(id)
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.saveUserEmail(email)
    }

    suspend fun saveRole(role: String) {
        dataStore.saveRole(role)
    }

}