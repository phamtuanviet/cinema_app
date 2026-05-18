package com.example.myapplication.presentation.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.AppDataStore
import com.example.myapplication.core.datastore.SessionManager
import com.google.firebase.messaging.FirebaseMessaging

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject


import com.example.myapplication.domain.repository.AuthRepository // Nhúng Repository chứa API gọi /me và logout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await

import retrofit2.HttpException
import java.io.IOException

@HiltViewModel
class AppViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository // 🔥 Thêm AuthRepository để gọi API
) : ViewModel() {

    // =========================
    // 🔥 APP STATE
    // =========================
    private val _appState = MutableStateFlow(AppState())
    val appState = _appState.asStateFlow()

    private val _deepLinkNavigationRoute = MutableStateFlow<String?>(null)
    val deepLinkNavigationRoute: StateFlow<String?> = _deepLinkNavigationRoute.asStateFlow()

    private val _paymentResult = MutableStateFlow<PaymentResult?>(null)
    val paymentResult: StateFlow<PaymentResult?> = _paymentResult.asStateFlow()

    init {
        observeAppState()
    }

    // =========================
    // 🔥 LẮNG NGHE DATASTORE ĐỂ CẬP NHẬT TRẠNG THÁI APP
    // =========================
    private fun observeAppState() {
        viewModelScope.launch {
            combine(
                sessionManager.accessTokenFlow,
                sessionManager.hasOnboardedFlow,
                sessionManager.darkThemeFlow,
                sessionManager.userFlow
            ) { token, onboarded, theme, user ->

                AppState(
                    isLoggedIn = !token.isNullOrEmpty(),
                    hasOnboarded = onboarded,
                    darkTheme = theme,
                    role = user?.role,
                    isBanned = user?.isBanned == true, // Lấy trạng thái khóa từ DataStore
                    isLoading = false
                )
            }.collect { state ->
                _appState.value = state
            }
        }
    }


    // =========================
    // 🔥 KIỂM TRA TRẠNG THÁI LIVE CỦA NGƯỜI DÙNG (GỌI TẠI SPLASH SCREEN)
    // =========================
    fun verifyUserStatusAndNavigate(
        onSuccess: (String?) -> Unit,
        onBannedOrError: () -> Unit
    ) {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken()

            // Nếu chưa có token thì ra thẳng màn Auth
            if (token.isNullOrEmpty()) {
                onBannedOrError()
                return@launch
            }

            try {
                // Gọi API lấy thông tin MỚI NHẤT từ Server
                val result = authRepository.getCurrentUser()

                if (result.isSuccess) {
                    val user = result.getOrNull()

                    // Kiểm tra thêm một lớp bảo vệ an toàn
                    if (user?.isBanned == true) {
                        forceLogout()
                        onBannedOrError()
                    } else {
                        // Cập nhật lại DataStore để đồng bộ với Server
                        user?.let { sessionManager.saveUser(it) }
                        onSuccess(user?.role)
                    }
                } else {
                    // Xử lý lỗi trả về từ API (ví dụ 401 hoặc 403)
                    val exception = result.exceptionOrNull()
                    if (exception is HttpException && (exception.code() == 401 || exception.code() == 403)) {
                        Log.e("VERIFY", "Token hết hạn hoặc tài khoản bị Ban!")
                        forceLogout()
                        onBannedOrError()
                    } else {
                        // Lỗi Server (500) -> Dùng dữ liệu Local tạm thời
                        fallbackToLocalData(onSuccess, onBannedOrError)
                    }
                }
            } catch (e: IOException) {
                // Mất mạng -> Dùng dữ liệu Local
                fallbackToLocalData(onSuccess, onBannedOrError)
            }
        }
    }

    private suspend fun fallbackToLocalData(onSuccess: (String?) -> Unit, onBannedOrError: () -> Unit) {
        val localUser = sessionManager.getUser()
        if (localUser?.isBanned == true) {
            forceLogout()
            onBannedOrError()
        } else {
            onSuccess(localUser?.role)
        }
    }

    // =========================
    // 🔥 ÉP ĐĂNG XUẤT KHI PHÁT HIỆN LỖI HOẶC BỊ BAN
    // =========================
    fun forceLogout() {
        viewModelScope.launch {
            val refreshToken = sessionManager.getRefreshToken() ?: ""

            // 1. Gọi Repo để xử lý thu hồi Token Backend + Reset Firebase Token
            authRepository.logout(refreshToken)

            // 2. Xóa sạch Session dưới Local.
            // Lúc này observeAppState() sẽ bắt được token = null và tự động đá UI ra màn Auth.
            sessionManager.clearSession()
        }
    }

    // =========================
    // 🔥 FIREBASE & THEME
    // =========================
    fun syncFcmToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Dùng .await() thay vì addOnCompleteListener
                val token = FirebaseMessaging.getInstance().token.await()
                sessionManager.saveFcmToken(token)

                // (Tùy chọn) Đảm bảo chắc chắn máy này đã đưng ký topic
                FirebaseMessaging.getInstance().subscribeToTopic("ALL_USERS").await()

            } catch (e: Exception) {
                Log.e("FCM", "Lỗi sync FCM: ${e.message}")
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            sessionManager.updateDarkTheme(!_appState.value.darkTheme)
        }
    }

    // =========================
    // 🔥 NAVIGATION & PAYMENT HANDLERS
    // =========================
    fun setDeepLinkNavigationRoute(route: String) {
        _deepLinkNavigationRoute.value = route
    }

    fun clearDeepLinkNavigationRoute() {
        _deepLinkNavigationRoute.value = null
    }

    fun onPaymentResult(code: String?, txnRef: String?) {
        Log.d("AppViewModel", "onPaymentResult: $code, $txnRef")
        _paymentResult.value = PaymentResult(code, txnRef)
    }

    fun clearPaymentResult() {
        _paymentResult.value = null
    }
}