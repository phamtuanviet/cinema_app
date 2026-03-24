package com.example.myapplication.presentation.screen.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        // Xóa thông báo lỗi khi người dùng bắt đầu nhập lại
        _state.value = _state.value.copy(
            email = email,
            error = null
        )
    }

    fun sendOtp() {
        val email = _state.value.email.trim()

        // 1. Kiểm tra rỗng
        if (email.isEmpty()) {
            _state.value = _state.value.copy(error = "Email không được để trống")
            return
        }

        // 2. Kiểm tra định dạng Email bằng Regex
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(emailRegex)) {
            _state.value = _state.value.copy(error = "Định dạng email không hợp lệ")
            return
        }

        // 3. Dữ liệu hợp lệ -> Gọi API
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val response = repository.forgotPassword(email)

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = response,
                    error = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gửi mã xác thực thất bại. Vui lòng thử lại!"
                )
            }
        }
    }
}