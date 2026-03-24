package com.example.myapplication.presentation.screen.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onFullNameChange(value: String) {
        _state.value = _state.value.copy(fullName = value, error = null)
    }

    fun onEmailChange(value: String) {
        _state.value = _state.value.copy(email = value, error = null)
    }

    fun onPhoneChange(value: String) {
        _state.value = _state.value.copy(phone = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _state.value = _state.value.copy(password = value, error = null)
    }

    fun register() {
        val fullName = _state.value.fullName.trim()
        val email = _state.value.email.trim()
        val phone = _state.value.phone.trim()
        val password = _state.value.password

        // 1. Validate Họ và tên
        if (fullName.isEmpty()) {
            _state.value = _state.value.copy(error = "Họ và tên không được để trống")
            return
        }

        // 2. Validate Email
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (email.isEmpty()) {
            _state.value = _state.value.copy(error = "Email không được để trống")
            return
        } else if (!email.matches(emailRegex)) {
            _state.value = _state.value.copy(error = "Định dạng email không hợp lệ")
            return
        }

        // 3. Validate Số điện thoại (Chỉ cho phép số, độ dài 9-11 số)
        val phoneRegex = "^[0-9]{9,11}$".toRegex()
        if (phone.isEmpty()) {
            _state.value = _state.value.copy(error = "Số điện thoại không được để trống")
            return
        } else if (!phone.matches(phoneRegex)) {
            _state.value = _state.value.copy(error = "Số điện thoại không hợp lệ (9-11 số)")
            return
        }

        // 4. Validate Mật khẩu
        if (password.isEmpty()) {
            _state.value = _state.value.copy(error = "Mật khẩu không được để trống")
            return
        } else if (password.length < 6) {
            _state.value = _state.value.copy(error = "Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        // 5. Dữ liệu hợp lệ -> Gọi API
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                Log.d("RegisterViewModel", "Bắt đầu đăng ký cho: $email")
                val response = repository.register(
                    email = email,
                    password = password,
                    fullName = fullName,
                    phone = phone
                )
                Log.d("RegisterViewModel", "Kết quả: $response")

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = response,
                    error = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Đăng ký thất bại. Vui lòng thử lại!"
                )
            }
        }
    }
}