package com.example.myapplication.presentation.screen.auth.reset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ResetPasswordState())
    val state = _state.asStateFlow()

    private var resetToken: String = ""

    fun setResetToken(token: String) {
        resetToken = token
    }

    fun onPasswordChange(password: String) {
        // Xóa lỗi khi người dùng bắt đầu gõ lại
        _state.value = _state.value.copy(password = password, error = null)
    }

    fun onConfirmPasswordChange(confirm: String) {
        // Xóa lỗi khi người dùng bắt đầu gõ lại
        _state.value = _state.value.copy(confirmPassword = confirm, error = null)
    }

    fun resetPassword() {
        val password = _state.value.password
        val confirm = _state.value.confirmPassword

        // 1. Kiểm tra rỗng
        if (password.isEmpty() || confirm.isEmpty()) {
            _state.value = _state.value.copy(error = "Vui lòng nhập đầy đủ mật khẩu")
            return
        }

        // 2. Kiểm tra độ dài
        if (password.length < 6) {
            _state.value = _state.value.copy(error = "Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        // 3. Kiểm tra khớp mật khẩu
        if (password != confirm) {
            _state.value = _state.value.copy(error = "Mật khẩu xác nhận không khớp")
            return
        }

        // 4. Dữ liệu hợp lệ -> Gọi API
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val response = repository.resetPassword(
                    resetToken,
                    password
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = response,
                    error = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Đặt lại mật khẩu thất bại. Vui lòng thử lại!"
                )
            }
        }
    }
}