package com.example.myapplication.presentation.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.AppDataStore
import com.example.myapplication.core.datastore.SessionManager

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

    fun clearPaymentResult() {
        _paymentResult.value = null
    }
}