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
                    , user.role,user.avatarUrl))

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
        // 1. Dọn dẹp dữ liệu cũ ngay lập tức
        sessionManager.clearTokens()
        sessionManager.clearUser()
        sessionManager.clearFcmToken() // Xóa token cũ TRƯỚC KHI lấy token mới

        // 2. Xử lý Firebase trên một Coroutine độc lập (tránh block luồng đăng xuất)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Bước 2.1: Xóa Token cũ trên Firebase
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM", "Đã xóa token cũ thành công do đăng xuất")

                        // Bước 2.2: Lấy Token mới tinh
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { newTokenTask ->
                            if (newTokenTask.isSuccessful) {
                                val newToken = newTokenTask.result
                                Log.d("FCM", "Token mới: $newToken")

                                // Lại dùng launch để gọi hàm suspend bên trong callback
                                CoroutineScope(Dispatchers.IO).launch {
                                    sessionManager.saveFcmToken(newToken)
                                }
                            } else {
                                Log.e("FCM", "Lỗi khi lấy token mới: ${newTokenTask.exception}")
                            }
                        }
                    } else {
                        Log.e("FCM", "Lỗi khi xóa token: ${task.exception}")
                    }
                }
            } catch (e: Exception) {
                Log.e("LOGOUT", "Lỗi trong quá trình xử lý Firebase: ${e.message}")
            }
        }

        return true
    }
}