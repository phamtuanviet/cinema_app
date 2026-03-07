package com.example.myapplication.presentation.screen.auth.verifyforgot

import com.example.myapplication.domain.repository.AuthRepository


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyForgotPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VerifyForgotPasswordState())
    val state = _state.asStateFlow()

    fun setEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onOtpChange(otp: String) {
        _state.value = _state.value.copy(otp = otp)
    }

    fun verifyOtp() {

        val email = _state.value.email
        val otp = _state.value.otp

        if (otp.length != 6) {
            _state.value = _state.value.copy(
                error = "OTP must be 6 digits"
            )
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {

                val token = repository.verifyForgotPassword(email, otp)

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    resetToken = token
                )



            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Verification failed"
                )

            }

        }
    }
}