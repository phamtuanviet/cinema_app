package com.example.myapplication.presentation.screen.admin.news.list

import com.example.myapplication.domain.repository.AdminNewsRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminNewsDto
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.NewsTypeTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminNewsListState(
    val newsList: List<AdminNewsDto> = emptyList(),
    val isLoadingFirstPage: Boolean = true,
    val isPaginating: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val currentTab: NewsTypeTab = NewsTypeTab.NORMAL, // Mặc định hiển thị tab Tin tức

    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

@HiltViewModel
class AdminNewsListViewModel @Inject constructor(
    private val repository: AdminNewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminNewsListState())
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
            delay(500)
            loadFirstPage()
        }
    }

    fun onTabSelected(tab: NewsTypeTab) {
        if (_state.value.currentTab == tab) return
        _state.update { it.copy(currentTab = tab, searchQuery = "") } // Chuyển tab thì reset ô search cho gọn
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoadingFirstPage = true, error = null, currentPage = 0, isLastPage = false, newsList = emptyList())
            }

            val currentState = _state.value
            val result = repository.getNews(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                type = currentState.currentTab.typeValue,
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
            val result = repository.getNews(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                type = currentState.currentTab.typeValue,
                page = nextPage
            )

            handleResult(result, isFirstPage = false, nextPage = nextPage)
        }
    }

    private fun handleResult(result: Result<AdminPaginatedResponse<AdminNewsDto>>, isFirstPage: Boolean, nextPage: Int = 0) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            _state.update { currentState ->
                val newItems = if (isFirstPage) response?.content.orEmpty() else currentState.newsList + response?.content.orEmpty()
                currentState.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    newsList = newItems,
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