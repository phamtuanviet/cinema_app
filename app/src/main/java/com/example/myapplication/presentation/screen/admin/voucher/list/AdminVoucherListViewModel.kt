package com.example.myapplication.presentation.screen.admin.voucher.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminVoucherDto
import com.example.myapplication.data.remote.dto.VoucherStatusTab
import com.example.myapplication.domain.repository.AdminVoucherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminVoucherListState(
    val vouchers: List<AdminVoucherDto> = emptyList(),
    val isLoadingFirstPage: Boolean = true,
    val isPaginating: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val currentTab: VoucherStatusTab = VoucherStatusTab.VALID, // Mặc định xem voucher đang dùng được

    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

@HiltViewModel
class AdminVoucherListViewModel @Inject constructor(
    private val repository: AdminVoucherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminVoucherListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFirstPage()
    }

    fun onSearchQueryChange(query: String) {
        // Tự động viết hoa mã Code khi tìm kiếm
        val upperQuery = query.uppercase()
        if (_state.value.searchQuery == upperQuery) return

        _state.update { it.copy(searchQuery = upperQuery) }
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500)
            loadFirstPage()
        }
    }

    fun onTabSelected(tab: VoucherStatusTab) {
        if (_state.value.currentTab == tab) return
        _state.update { it.copy(currentTab = tab) }
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoadingFirstPage = true, error = null, currentPage = 0, isLastPage = false, vouchers = emptyList())
            }

            val currentState = _state.value
            val result = repository.getVouchers(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                status = currentState.currentTab.statusValue,
                page = 0
            )

            handleResult(result, isFirstPage = true)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isLoadingFirstPage || currentState.isPaginating || currentState.isLastPage) return

        viewModelScope.launch {
            _state.update { it.copy(isPaginating = true, error = null) }

            val nextPage = currentState.currentPage + 1
            val result = repository.getVouchers(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                status = currentState.currentTab.statusValue,
                page = nextPage
            )

            handleResult(result, isFirstPage = false, nextPage = nextPage)
        }
    }

    private fun handleResult(result: Result<AdminPaginatedResponse<AdminVoucherDto>>, isFirstPage: Boolean, nextPage: Int = 0) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            _state.update { currentState ->
                val newItems = if (isFirstPage) response?.content.orEmpty() else currentState.vouchers + response?.content.orEmpty()
                currentState.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    vouchers = newItems,
                    currentPage = nextPage,
                    isLastPage = response?.isLast ?: true
                )
            }
        } else {
            _state.update {
                it.copy(isLoadingFirstPage = false, isPaginating = false, error = result.exceptionOrNull()?.message)
            }
        }
    }
}