package com.example.myapplication.presentation.screen.auth.verifyforgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
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
        // Chỉ cho phép nhập số và tự động chặn lại ở 6 ký tự
        val filtered = otp.filter { it.isDigit() }.take(6)
        _state.value = _state.value.copy(otp = filtered, error = null) // Xóa lỗi khi gõ lại
    }

    fun verifyOtp() {
        val email = _state.value.email
        val otp = _state.value.otp

        // 1. Kiểm tra độ dài OTP
        if (otp.length != 6) {
            _state.value = _state.value.copy(error = "Mã OTP phải bao gồm đúng 6 chữ số")
            return
        }

        // 2. Gọi API
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                // Nhận lại token từ server để cho phép đổi mật khẩu
                val token = repository.verifyForgotPassword(email, otp)

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    resetToken = token,
                    error = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Xác thực thất bại. Vui lòng kiểm tra lại mã OTP!"
                )
            }
        }
    }
}