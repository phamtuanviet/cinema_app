package com.example.myapplication.presentation.screen.admin.combo.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminComboDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.ComboStatusTab
import com.example.myapplication.domain.repository.AdminComboRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminComboListState(
    val combos: List<AdminComboDto> = emptyList(),
    val isLoadingFirstPage: Boolean = true,
    val isPaginating: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val currentTab: ComboStatusTab = ComboStatusTab.ACTIVE, // Mặc định hiển thị Combo đang bán

    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

@HiltViewModel
class AdminComboListViewModel @Inject constructor(
    private val repository: AdminComboRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminComboListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFirstPage()
    }

    fun onSearchQueryChange(query: String) {
        if (_state.value.searchQuery == query) return

        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500) // Debounce 0.5 giây
            loadFirstPage()
        }
    }

    fun onTabSelected(tab: ComboStatusTab) {
        if (_state.value.currentTab == tab) return
        _state.update { it.copy(currentTab = tab) }
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoadingFirstPage = true, error = null, currentPage = 0, isLastPage = false, combos = emptyList())
            }

            val currentState = _state.value
            val result = repository.getCombos(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                isActive = currentState.currentTab.isActiveValue,
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
            val result = repository.getCombos(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                isActive = currentState.currentTab.isActiveValue,
                page = nextPage
            )

            handleResult(result, isFirstPage = false, nextPage = nextPage)
        }
    }

    private fun handleResult(result: Result<AdminPaginatedResponse<AdminComboDto>>, isFirstPage: Boolean, nextPage: Int = 0) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            _state.update { currentState ->
                val newItems = if (isFirstPage) response?.content.orEmpty() else currentState.combos + response?.content.orEmpty()
                currentState.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    combos = newItems,
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