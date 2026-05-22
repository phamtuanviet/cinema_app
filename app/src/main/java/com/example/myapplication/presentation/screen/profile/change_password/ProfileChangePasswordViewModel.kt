package com.example.myapplication.presentation.screen.profile.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.update

@HiltViewModel
class ProfileChangePasswordViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileChangePasswordState())
    val state: StateFlow<ProfileChangePasswordState> = _state

    fun onOldPasswordChange(value: String) {
        _state.update { it.copy(oldPassword = value) }
    }

    fun onNewPasswordChange(value: String) {
        _state.update { it.copy(newPassword = value) }
    }

    fun changePassword() {
        val currentState = _state.value
        val oldPass = currentState.oldPassword.trim()
        val newPass = currentState.newPassword.trim()

        // 1. Chặn nhập rỗng
        if (oldPass.isBlank() || newPass.isBlank()) {
            _state.update { it.copy(isError = true, message = "Vui lòng nhập đầy đủ mật khẩu!") }
            return
        }

        // 2. Chặn mật khẩu quá ngắn
        if (newPass.length < 6) {
            _state.update { it.copy(isError = true, message = "Mật khẩu mới phải có ít nhất 6 ký tự!") }
            return
        }

        // 3. Chặn đặt lại mật khẩu y như cũ
        if (oldPass == newPass) {
            _state.update { it.copy(isError = true, message = "Mật khẩu mới phải khác mật khẩu hiện tại!") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false) }

            val result = repository.changePassword(oldPass, newPass)

            result.fold(
                onSuccess = {
                    _state.update { it.copy(
                        isLoading = false,
                        isSuccess = true,
                        message = "Đổi mật khẩu thành công!" // Dùng tiếng Việt thay vì lấy từ Backend
                    )}
                },
                onFailure = { e ->
                    _state.update { it.copy(
                        isLoading = false,
                        isError = true,
                        message = getFriendlyErrorMessage(e)
                    )}
                }
            )
        }
    }

    // Xóa trạng thái lỗi sau khi đã hiện Toast
    fun clearError() {
        _state.update { it.copy(isError = false, message = "") }
    }

    // Bộ dịch lỗi kỹ thuật sang tiếng Việt thân thiện
    private fun getFriendlyErrorMessage(e: Throwable): String {
        val msg = e.message?.lowercase() ?: ""
        return when {
            "incorrect" in msg || "wrong" in msg || "match" in msg -> "Mật khẩu hiện tại không chính xác."
            "not found" in msg -> "Không tìm thấy tài khoản."
            else -> "Đổi mật khẩu thất bại. Vui lòng thử lại sau!"
        }
    }
}