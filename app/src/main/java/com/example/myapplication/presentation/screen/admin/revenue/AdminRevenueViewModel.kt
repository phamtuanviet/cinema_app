package com.example.myapplication.presentation.screen.admin.revenue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.RevenueSummaryDto
import com.example.myapplication.domain.repository.AdminRevenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

enum class TimeRangeFilter(val label: String, val apiValue: String) {
    TODAY("Hôm nay", "DAY"),
    THIS_WEEK("Tuần này", "WEEK"),
    THIS_MONTH("Tháng này", "MONTH"),
    THIS_YEAR("Năm nay", "YEAR")
}

data class AdminRevenueState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: TimeRangeFilter = TimeRangeFilter.THIS_WEEK,
    val currentDate: LocalDate = LocalDate.now(),
    val summary: RevenueSummaryDto? = null
)

@HiltViewModel
class AdminRevenueViewModel @Inject constructor(
    private val repository: AdminRevenueRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminRevenueState())
    val state = _state.asStateFlow()

    init {
        // 🔥 Đã sửa: Không cần truyền tham số
        loadRevenueData()
    }

    fun refresh() {
        // 🔥 Đã sửa: Không cần truyền tham số
        loadRevenueData()
    }

    fun onFilterSelected(filter: TimeRangeFilter) {
        if (_state.value.selectedFilter == filter) return
        _state.update { it.copy(
            selectedFilter = filter,
            currentDate = LocalDate.now()
        ) }
        loadRevenueData()
    }

    fun previousPeriod() {
        val current = _state.value.currentDate
        val newDate = when (_state.value.selectedFilter) {
            TimeRangeFilter.TODAY -> current.minusDays(1)
            TimeRangeFilter.THIS_WEEK -> current.minusWeeks(1)
            TimeRangeFilter.THIS_MONTH -> current.minusMonths(1)
            TimeRangeFilter.THIS_YEAR -> current.minusYears(1)
        }
        _state.update { it.copy(currentDate = newDate) }
        loadRevenueData()
    }

    fun nextPeriod() {
        val current = _state.value.currentDate
        val newDate = when (_state.value.selectedFilter) {
            TimeRangeFilter.TODAY -> current.plusDays(1)
            TimeRangeFilter.THIS_WEEK -> current.plusWeeks(1)
            TimeRangeFilter.THIS_MONTH -> current.plusMonths(1)
            TimeRangeFilter.THIS_YEAR -> current.plusYears(1)
        }
        _state.update { it.copy(currentDate = newDate) }
        loadRevenueData()
    }

    // 🔥 Đã sửa: Bỏ tham số filter đi vì dùng trực tiếp từ _state
    private fun loadRevenueData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val dateStr = _state.value.currentDate.toString()

            // Gọi API
            val result = repository.getRevenueSummary(_state.value.selectedFilter.apiValue, dateStr)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        summary = result.getOrNull()
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Lỗi tải doanh thu từ máy chủ"
                    )
                }
            }
        }
    }
}

fun formatCurrentPeriod(filter: TimeRangeFilter, date: LocalDate): String {
    return when (filter) {
        TimeRangeFilter.TODAY -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        TimeRangeFilter.THIS_WEEK -> "Tuần của ${date.format(DateTimeFormatter.ofPattern("dd/MM"))}"
        TimeRangeFilter.THIS_MONTH -> "Tháng ${date.monthValue}, ${date.year}"
        TimeRangeFilter.THIS_YEAR -> "Năm ${date.year}"
    }
}

fun isFuture(filter: TimeRangeFilter, date: LocalDate): Boolean {
    val today = LocalDate.now()
    return when (filter) {
        TimeRangeFilter.TODAY -> !date.isBefore(today)
        TimeRangeFilter.THIS_WEEK -> !date.minusDays(7).isBefore(today)
        TimeRangeFilter.THIS_MONTH -> date.year >= today.year && date.monthValue >= today.monthValue
        TimeRangeFilter.THIS_YEAR -> date.year >= today.year
    }
}