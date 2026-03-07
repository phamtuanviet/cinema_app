package com.example.myapplication.core.datastore




import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.example.myapplication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {

        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val HAS_ONBOARDED = booleanPreferencesKey("has_onboarded")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val ROLE = stringPreferencesKey("role")


    }

    val accessTokenFlow = dataStore.data.map {
        it[ACCESS_TOKEN] ?: ""
    }

    val refreshTokenFlow = dataStore.data.map {
        it[REFRESH_TOKEN] ?: ""
    }

    val darkThemeFlow = dataStore.data.map {
        it[DARK_THEME] ?: false
    }

    val hasOnboardedFlow = dataStore.data.map {
        it[HAS_ONBOARDED] ?: false
    }

    val userIdFlow = dataStore.data.map {
        it[USER_ID] ?: ""
    }

    val userEmailFlow = dataStore.data.map {
        it[USER_EMAIL] ?: ""
    }

    val roleFlow = dataStore.data.map {
        it[ROLE] ?: ""
    }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit {
            it[ACCESS_TOKEN] = token
        }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit {
            it[REFRESH_TOKEN] = token
        }
    }

    suspend fun saveDarkTheme(value: Boolean) {
        dataStore.edit {
            it[DARK_THEME] = value
        }
    }

    suspend fun saveOnboarded(value: Boolean) {
        dataStore.edit {
            it[HAS_ONBOARDED] = value
        }
    }

    suspend fun clearToken() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
        }
    }

    suspend fun saveUserId(id: String) {
        dataStore.edit {
            it[USER_ID] = id
        }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit {
            it[USER_EMAIL] = email
        }
    }

    suspend fun saveRole(role: String) {
        dataStore.edit {
            it[ROLE] = role
        }
    }
}

