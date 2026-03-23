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

    suspend fun getUser(): UserDto? {
        return dataStore.userFlow.first()
    }

    // ================= CHECK =================
    suspend fun isLoggedIn(): Boolean {
        val token = getAccessToken()
        return !token.isNullOrEmpty()
    }

    // ================= COMBINE STATE =================
    val sessionFlow: Flow<Pair<UserDto?, String?>> =
        combine(userFlow, accessTokenFlow) { user, token ->
            Pair(user, token)
        }

    // ================= UPDATE =================
    suspend fun updateUser(user: UserDto) {
        dataStore.saveUser(user)
    }

    suspend fun updateAccessToken(newAccessToken: String) {
        val refresh = getRefreshToken() ?: return
        dataStore.saveTokens(newAccessToken, refresh)
    }

    // ================= CLEAR =================
    suspend fun logout() {
        dataStore.clearAll()
    }
}