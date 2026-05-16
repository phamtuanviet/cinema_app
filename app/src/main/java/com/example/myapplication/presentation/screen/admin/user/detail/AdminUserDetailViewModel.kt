package com.example.myapplication.presentation.screen.admin.user.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminUserDetailDto
import com.example.myapplication.data.remote.dto.UserDetailTab
import com.example.myapplication.domain.repository.AdminUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AdminUserDetailState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentTab: UserDetailTab = UserDetailTab.VOUCHER,
    val userDetail: AdminUserDetailDto? = null
)

@HiltViewModel
class AdminUserDetailViewModel @Inject constructor(
    private val repository: AdminUserRepository, // 🔥 Nhúng Repository vào đây
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle["userId"])
    private val _state = MutableStateFlow(AdminUserDetailState())
    val state = _state.asStateFlow()

    init {
        loadUserDetail()
    }

    private fun loadUserDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // 🔥 Gọi API thật thông qua Repository
            val result = repository.getUserDetail(userId)

            if (result.isSuccess) {
                _state.update {
                    it.copy(isLoading = false, userDetail = result.getOrNull())
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Lỗi tải dữ liệu")
                }
            }
        }
    }

    fun onTabSelected(tab: UserDetailTab) {
        _state.update { it.copy(currentTab = tab) }
    }
}