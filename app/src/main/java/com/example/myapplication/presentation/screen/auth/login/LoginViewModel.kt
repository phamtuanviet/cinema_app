package com.example.myapplication.presentation.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        // Xóa thông báo lỗi khi người dùng bắt đầu nhập lại
        _state.value = _state.value.copy(email = email, error = null)
    }

    fun onPasswordChange(password: String) {
        // Xóa thông báo lỗi khi người dùng bắt đầu nhập lại
        _state.value = _state.value.copy(password = password, error = null)
    }

    fun login() {
        val email = _state.value.email.trim()
        val password = _state.value.password

        // 1. Kiểm tra Email
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (email.isEmpty()) {
            _state.value = _state.value.copy(error = "Email không được để trống")
            return
        } else if (!email.matches(emailRegex)) {
            _state.value = _state.value.copy(error = "Định dạng email không hợp lệ")
            return
        }

        // 2. Kiểm tra Mật khẩu
        if (password.isEmpty()) {
            _state.value = _state.value.copy(error = "Mật khẩu không được để trống")
            return
        } else if (password.length < 6) {
            _state.value = _state.value.copy(error = "Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        // 3. Dữ liệu hợp lệ -> Bắt đầu gọi API
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val result = repository.login(
                    email = email,
                    password = password
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = null,
                    isSuccess = result
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Đăng nhập thất bại. Vui lòng thử lại!"
                )
            }
        }
    }
}