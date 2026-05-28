package com.example.myapplication.presentation.screen.admin.banner.list

import com.example.myapplication.domain.repository.AdminBannerRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminBannerDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.BannerActionTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminBannerListState(
    val banners: List<AdminBannerDto> = emptyList(),
    val isLoadingFirstPage: Boolean = true,
    val isPaginating: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val currentTab: BannerActionTab = BannerActionTab.MOVIE, // Mặc định mở Tab Phim

    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

@HiltViewModel
class AdminBannerListViewModel @Inject constructor(
    private val repository: AdminBannerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminBannerListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
//        loadFirstPage()
    }

    fun prepareForReturn() {
        _state.update {
            it.copy(
                banners = emptyList(),
                isLoadingFirstPage = true,
                error = null
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        if (_state.value.searchQuery == query) return

        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        // Debounce 0.5s chống spam API
        searchJob = viewModelScope.launch {
            delay(500)
            loadFirstPage()
        }
    }

    fun resetBannerList() {
        _state.update { it.copy(banners = emptyList()) }
    }

    fun onTabSelected(tab: BannerActionTab) {
        if (_state.value.currentTab == tab) return
        // Reset ô tìm kiếm khi đổi Tab
        _state.update { it.copy(currentTab = tab, searchQuery = "") }
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoadingFirstPage = true, error = null, currentPage = 0, isLastPage = false, banners = emptyList())
            }

            val currentState = _state.value
            val result = repository.getBanners(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                actionType = currentState.currentTab.typeValue,
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
            val result = repository.getBanners(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                actionType = currentState.currentTab.typeValue,
                page = nextPage
            )

            handleResult(result, isFirstPage = false, nextPage = nextPage)
        }
    }

    private fun handleResult(result: Result<AdminPaginatedResponse<AdminBannerDto>>, isFirstPage: Boolean, nextPage: Int = 0) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            _state.update { currentState ->
                val newItems = if (isFirstPage) response?.content.orEmpty() else currentState.banners + response?.content.orEmpty()
                currentState.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    banners = newItems,
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