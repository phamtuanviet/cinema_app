package com.example.myapplication.core.datastore

import com.example.myapplication.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: AppDataStore
) {

    // ================= SAVE =================
    suspend fun saveUser(
        user: UserDto,

    ) {
        dataStore.saveUser(user)
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        dataStore.saveTokens(accessToken, refreshToken)
    }

    suspend fun clearTokens() {
        dataStore.clearTokens()
    }

    suspend fun clearUser() {
        dataStore.clearUser()
    }

    // ================= GET (FLOW) =================
    val userFlow: Flow<UserDto?> = dataStore.userFlow

    val accessTokenFlow: Flow<String?> = dataStore.accessTokenFlow

    val refreshTokenFlow: Flow<String?> = dataStore.refreshTokenFlow

    val hasOnboardedFlow: Flow<Boolean> = dataStore.hasOnboardedFlow

    val darkThemeFlow: Flow<Boolean> = dataStore.darkThemeFlow


    // ================= GET (ONE TIME) =================
    suspend fun getAccessToken(): String? {
        return dataStore.accessTokenFlow.first()
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.refreshTokenFlow.first()
    }

    suspend fun updateDarkTheme(isDark: Boolean) {
        dataStore.saveDarkTheme(isDark)
    }



    suspend fun getUser(): UserDto? {
        return dataStore.userFlow.first()
    }

    suspend fun saveFcmToken(token: String) {
        dataStore.saveFcmToken(token)
    }

    suspend fun clearFcmToken() {
        dataStore.clearFcmToken()
    }

    val fcmTokenFlow: Flow<String?> = dataStore.fcmTokenFlow

    suspend fun getFcmToken(): String? {
        return dataStore.fcmTokenFlow.first()
    }


}