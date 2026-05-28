package com.example.myapplication.data.remote.repository

import android.util.Log
import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.data.remote.dto.ForgotPasswordRequest
import com.example.myapplication.data.remote.dto.LoginRequest
import com.example.myapplication.data.remote.dto.LogoutRequest
import com.example.myapplication.data.remote.dto.RegisterRequest
import com.example.myapplication.data.remote.dto.ResetPasswordRequest
import com.example.myapplication.data.remote.dto.UserDto
import com.example.myapplication.data.remote.dto.VerifyEmailRequest
import com.example.myapplication.data.remote.dto.VerifyForgotPasswordRequest
import com.example.myapplication.domain.repository.AuthRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
        fcmToken : String?
    ): Boolean {


        return try {
            Log.d("LOGIN_DEBUG", "Refresh goi lan $password'")
            val response = authApi.login(
                LoginRequest(
                    email = email,
                    password = password,
                    fcmToken = fcmToken
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                    ?: throw Exception("Empty response")

                val user = response.body()!!.user;

                sessionManager.saveTokens(
                    response.body()!!.accessToken,
                    response.body()!!.refreshToken
                )
                sessionManager.saveUser(UserDto(user.id, user.email
                    , user.fullName, user.phone, user.isVerified
                    , user.role,user.avatarUrl,user.isBanned))

                return true;
            } else {

                throw Exception("Login failed")
            }
        } catch (e: Exception) {

            Log.e("LOGIN_ERROR", e.toString())
            false
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): Boolean {
        return try {
            val response = authApi.register(RegisterRequest(email, password, fullName, phone))
            if (response.isSuccessful) {
                val body = response.body()
                    ?: throw Exception("Empty response")
                return true
            } else {
                throw Exception("Register failed")
            }
        } catch (E: Exception) {
            Log.e("REGISTER_ERROR", E.toString())
            false
        }

    }

    override suspend fun verifyEmail(
        email: String,
        otp: String
    ): Boolean {
        val response = authApi.verifyEmail(VerifyEmailRequest(email, otp))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")


            return true;
        } else {
            throw Exception("Verify failed")
        }
    }

    override suspend fun forgotPassword(
        email: String
    ): Boolean {
        val response = authApi.forgotPassword(ForgotPasswordRequest(email))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")

            return true;
        } else {
            throw Exception("Forgot password failed")
        }
    }

    override suspend fun verifyForgotPassword(
        email: String,
        otp: String
    ): String {
        val response = authApi.verifyForgotPassword(VerifyForgotPasswordRequest(email, otp))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")
            return body.resetToken;
        } else {
            throw Exception("Verify failed")
        }
    }

    override suspend fun resetPassword(
        resetToken: String,
        password: String
    ): Boolean {
        val response = authApi.resetPassword(ResetPasswordRequest(resetToken, password))
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw Exception("Empty response")

            return true;
        } else {
            throw Exception("Reset password failed")
        }
    }

    override suspend fun logout(refreshToken: String): Boolean {
        val currentFcmToken = sessionManager.getFcmToken()

        try {
            val request = LogoutRequest(refreshToken = refreshToken, fcmToken = currentFcmToken)
            authApi.logout(request)
        } catch (e: Exception) {
            Log.e("LOGOUT", "Không thể gọi API logout tới server: ${e.message}")
        }
        // 1. Dọn dẹp dữ liệu cũ ngay lập tức
        sessionManager.clearTokens()
        sessionManager.clearUser()
        sessionManager.clearFcmToken()

        // 2. Dùng launch để "bắn" luồng chạy ngầm (Không block luồng đăng xuất)
        // Nhờ vậy hàm sẽ return true ngay lập tức -> Giao diện chuyển mượt mà
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val firebaseMessaging = FirebaseMessaging.getInstance()

                // Các lệnh Firebase vẫn dùng .await() để code tuần tự, sạch sẽ, không bị lồng nhau
                firebaseMessaging.deleteToken().await()
                Log.d("FCM", "Đã xóa token cũ thành công do đăng xuất")

                val newToken = firebaseMessaging.token.await()
                sessionManager.saveFcmToken(newToken)
                Log.d("FCM", "Token mới cho khách: $newToken")

                // Vẫn giữ lại lệnh đăng ký Topic để khách vẫn nhận được thông báo Phim mới
                firebaseMessaging.subscribeToTopic("ALL_USERS").await()
                Log.d("FCM", "Đã đăng ký lại topic ALL_USERS cho khách")

            } catch (e: Exception) {
                Log.e("LOGOUT", "Lỗi trong quá trình xử lý Firebase: ${e.message}")
            }
        }

        // 3. Trả về true lập tức để UI bay thẳng ra màn hình Login
        return true
    }

    override suspend fun getCurrentUser() = runCatching { authApi.getCurrentUser() }

}