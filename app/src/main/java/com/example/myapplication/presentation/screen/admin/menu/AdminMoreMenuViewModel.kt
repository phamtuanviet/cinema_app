package com.example.myapplication.presentation.screen.admin.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.dto.UserDto
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminMoreMenuState(
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val error: String? = null,
    val user: UserDto? = null
)

@HiltViewModel
class AdminMoreMenuViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminMoreMenuState())
    val state = _state.asStateFlow()

    init {
        observeUser() // BẮT ĐẦU LẮNG NGHE USER KHI KHỞI TẠO
    }

    private fun observeUser() {
        viewModelScope.launch {
            sessionManager.userFlow.collect { user ->
                _state.update { it.copy(user = user) }
            }
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            val refresh = sessionManager.getRefreshToken()

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Gọi API backend để hủy token
                if (!refresh.isNullOrEmpty()) {
                    authRepository.logout(refresh)
                }
            } catch (e: Exception) {
                // Lỗi mạng hoặc server không block việc thoát ở local
            } finally {
                // Xóa sạch thông tin ở Local
                sessionManager.clearTokens()
                sessionManager.clearUser()

                // Cập nhật State để UI điều hướng
                _state.update {
                    it.copy(
                        isLoggedOut = true,
                        isLoading = false
                    )
                }
            }
        }
    }
}