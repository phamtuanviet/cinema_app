package com.example.myapplication.presentation.screen.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.AdminDashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val repository: AdminDashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminDashboardState())
    val state: StateFlow<AdminDashboardState> = _state

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Gọi song song 2 APIs cho nhanh
            val statsDeferred = async { repository.getDashboardStats() }
            val revenueDeferred = async { repository.getRevenueLast7Days() }

            val statsResult = statsDeferred.await()
            val revenueResult = revenueDeferred.await()

            if (statsResult.isSuccess && revenueResult.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        stats = statsResult.getOrNull(),
                        revenueData = revenueResult.getOrNull().orEmpty()
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = statsResult.exceptionOrNull()?.message
                            ?: revenueResult.exceptionOrNull()?.message
                            ?: "Lỗi không xác định khi tải Dashboard"
                    )
                }
            }
        }
    }
}