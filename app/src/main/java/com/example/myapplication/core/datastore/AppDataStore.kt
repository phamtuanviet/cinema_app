package com.example.myapplication.core.datastore




import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.example.myapplication.data.remote.dto.UserDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.jvm.java

class AppDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private val gson = Gson()
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER = stringPreferencesKey("user")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val HAS_ONBOARDED = booleanPreferencesKey("has_onboarded")
    }

    val accessTokenFlow = dataStore.data.map {
        it[ACCESS_TOKEN]
    }

    val refreshTokenFlow = dataStore.data.map {
        it[REFRESH_TOKEN]
    }

    suspend fun saveTokens(access: String, refresh: String) {
        dataStore.edit {
            it[ACCESS_TOKEN] = access
            it[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun clearTokens() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
        }
    }

    // ================= USER =================
    val userFlow = dataStore.data.map { prefs ->
        prefs[USER]?.let {
            gson.fromJson(it, UserDto::class.java)
        }
    }

    suspend fun saveUser(user: UserDto) {
        dataStore.edit {
            it[USER] = gson.toJson(user)
        }
    }

    suspend fun clearUser() {
        dataStore.edit {
            it.remove(USER)
        }
    }

    // ================= SETTINGS =================
    val darkThemeFlow = dataStore.data.map {
        it[DARK_THEME] ?: false
    }

    val hasOnboardedFlow = dataStore.data.map {
        it[HAS_ONBOARDED] ?: false
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

    // ================= CLEAR ALL =================
    suspend fun clearAll() {
        dataStore.edit {
            it.clear()
        }
    }
}

