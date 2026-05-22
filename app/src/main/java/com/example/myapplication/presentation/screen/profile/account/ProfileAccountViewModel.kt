package com.example.myapplication.presentation.screen.profile.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ProfileAccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileAccountState())
    val state: StateFlow<ProfileAccountState> = _state

    init {
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            sessionManager.userFlow.collect { user ->
                _state.value = _state.value.copy(
                    user = user,
                    fullName = user?.fullName ?: "",
                    isLoading = false
                )
            }
        }
    }

    fun onFullNameChange(value: String) {
        _state.value = _state.value.copy(fullName = value)
    }

    fun setAvatar(file: File) {
        _state.value = _state.value.copy(avatarFile = file)
    }

    // 🔥 LOGIC CẬP NHẬT CHUẨN CHUYÊN NGHIỆP
    fun updateProfile() {
        val current = _state.value
        val trimmedName = current.fullName.trim()

        // 1. Chặn nhập tên rỗng hoặc toàn dấu cách
        if (trimmedName.isBlank()) {
            _state.value = current.copy(error = "Họ và tên không được để trống!")
            return
        }

        // 2. Chặn gọi API nếu không có gì thay đổi (Tên y hệt cũ VÀ không có ảnh mới)
        val isNameUnchanged = trimmedName == current.user?.fullName
        if (isNameUnchanged && current.avatarFile == null) {
            _state.value = current.copy(error = "Bạn chưa thay đổi thông tin nào.")
            return
        }

        viewModelScope.launch {
            _state.value = current.copy(isUpdating = true, error = null)

            val result = userRepository.updateProfile(
                fullName = trimmedName, // Gửi tên đã xóa khoảng trắng thừa
                avatarFile = current.avatarFile
            )

            result.onSuccess { userDto ->
                sessionManager.saveUser(userDto)

                _state.value = _state.value.copy(
                    user = userDto,
                    avatarFile = null, // Giải phóng bộ nhớ ảnh sau khi up xong
                    isUpdating = false,
                    updateSuccess = true
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isUpdating = false,
                    error = error.message ?: "Cập nhật thất bại. Vui lòng thử lại!"
                )
            }
        }
    }

    fun clearFlags() {
        _state.value = _state.value.copy(
            updateSuccess = false,
            error = null
        )
    }
}