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
@HiltViewModel
class AppViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    // =========================
    // 🔥 APP STATE
    // =========================
    private val _appState = MutableStateFlow(AppState())
    val appState = _appState.asStateFlow()

    private val _deepLinkNavigationRoute = MutableStateFlow<String?>(null)
    val deepLinkNavigationRoute: StateFlow<String?> = _deepLinkNavigationRoute.asStateFlow()

    fun setDeepLinkNavigationRoute(route: String) {
        _deepLinkNavigationRoute.value = route
    }

    // Gọi sau khi Jetpack Compose đã navigate xong để tránh nhảy màn hình lại
    fun clearDeepLinkNavigationRoute() {
        _deepLinkNavigationRoute.value = null
    }

    // =========================
    // 🔥 PAYMENT RESULT (DEEP LINK)
    // =========================
    private val _paymentResult = MutableStateFlow<PaymentResult?>(null)
    val paymentResult: StateFlow<PaymentResult?> = _paymentResult

    init {
        observeAppState()
    }

    // =========================
    // 🔥 OBSERVE APP STATE
    // =========================
    private fun observeAppState() {
        viewModelScope.launch {

            combine(
                sessionManager.accessTokenFlow,
                sessionManager.hasOnboardedFlow,
                sessionManager.darkThemeFlow
            ) { token, onboarded, theme ->

                Log.d("AppViewModel", "token = $token")
                Log.d("AppViewModel", "onboarded = $onboarded")
                Log.d("AppViewModel", "darkTheme = $theme")

                AppState(
                    isLoggedIn = !token.isNullOrEmpty(),
                    hasOnboarded = onboarded,
                    darkTheme = theme,
                    isLoading = false
                )
            }.collect { state ->
                _appState.value = state
            }
        }
    }

    // =========================
    // 🔥 HANDLE DEEP LINK PAYMENT
    // =========================
    fun onPaymentResult(code: String?, txnRef: String?) {
        Log.d("AppViewModel", "onPaymentResult: $code, $txnRef")
        _paymentResult.value = PaymentResult(code, txnRef)
    }

    fun syncFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Lấy token thành công
            val token = task.result
            Log.d("FCM", "Token lúc mở app: $token")

            // Lưu vào DataStore
            viewModelScope.launch {
                sessionManager.saveFcmToken(token)
            }
        }
    }

    fun clearPaymentResult() {
        _paymentResult.value = null
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val current = _appState.value.darkTheme
            sessionManager.updateDarkTheme(!current)
        }
    }
}