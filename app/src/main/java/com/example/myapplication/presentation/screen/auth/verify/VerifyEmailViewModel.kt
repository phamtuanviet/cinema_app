package com.example.myapplication.presentation.screen.auth.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(VerifyEmailState())
    val state: StateFlow<VerifyEmailState> = _state.asStateFlow()

    fun setEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onOtpChange(value: String) {
        // Chỉ cho phép nhập số và tối đa 6 ký tự
        val filtered = value.filter { it.isDigit() }.take(6)
        _state.value = _state.value.copy(otp = filtered, error = null)
    }

    fun verify() {
        val current = _state.value

        // 1. Kiểm tra độ dài OTP
        if (current.otp.length != 6) {
            _state.value = current.copy(error = "Mã OTP phải bao gồm đúng 6 chữ số")
            return
        }

        // 2. Gọi API
        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null)

            try {
                val result = repository.verifyEmail(
                    email = current.email,
                    otp = current.otp
                )

                _state.value = current.copy(
                    isLoading = false,
                    isSuccess = result,
                    error = null
                )

            } catch (e: Exception) {
                _state.value = current.copy(
                    isLoading = false,
                    error = e.message ?: "Xác thực thất bại. Vui lòng kiểm tra lại mã OTP!"
                )
            }
        }
    }
}