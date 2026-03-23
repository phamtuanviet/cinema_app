//package com.example.myapplication.data.local
//
//import android.content.Context
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKey
//
//class TokenManager(context: Context) {
//
//    private val masterKey = MasterKey.Builder(context)
//        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//        .build()
//
//    private val prefs = EncryptedSharedPreferences.create(
//        context,
//        "secure_prefs",
//        masterKey,
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//    )
//
//    companion object {
//        private const val KEY_ACCESS_TOKEN = "access_token"
//        private const val KEY_REFRESH_TOKEN = "refresh_token"
//        private const val KEY_USER_STATE = "user_state"
//    }
//
//    // Lưu token
//    fun saveTokens(accessToken: String, refreshToken: String) {
//        prefs.edit().apply {
//            putString(KEY_ACCESS_TOKEN, accessToken)
//            putString(KEY_REFRESH_TOKEN, refreshToken)
//            apply()
//        }
//    }
//
//    // Lấy token
//    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
//    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
//
//    // Xóa token
//    fun clearTokens() {
//        prefs.edit().clear().apply()
//    }
//
//    // Lưu trạng thái user (ví dụ: JSON hoặc ID)
//    fun saveUserState(userJson: String) {
//        prefs.edit().putString(KEY_USER_STATE, userJson).apply()
//    }
//
//    fun getUserState(): String? = prefs.getString(KEY_USER_STATE, null)
//
//    fun clearUserState() {
//        prefs.edit().remove(KEY_USER_STATE).apply()
//    }
//}