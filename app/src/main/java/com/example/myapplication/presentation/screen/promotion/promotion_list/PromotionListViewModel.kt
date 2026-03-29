package com.example.myapplication.presentation.screen.promotion.promotion_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromotionListViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PromotionListState())
    val state: StateFlow<PromotionListState> = _state

    init {
        loadPosts(reset = true)
    }

    // 🔥 load data (có reset hoặc load thêm)
    fun loadPosts(reset: Boolean = false) {

        val currentState = _state.value

        if (currentState.isLoading || currentState.isLoadingMore) return
        if (!currentState.hasMore && !reset) return

        viewModelScope.launch {

            val nextPage = if (reset) 0 else currentState.page

            _state.value = currentState.copy(
                isLoading = reset,
                isLoadingMore = !reset,
                error = null
            )

            val result = repository.getPosts(
                type = currentState.selectedType,
                page = nextPage
            )

            result.onSuccess { data ->

                val newList = if (reset) data else currentState.posts + data

                _state.value = _state.value.copy(
                    posts = newList,
                    isLoading = false,
                    isLoadingMore = false,
                    page = nextPage + 1,
                    hasMore = data.isNotEmpty()
                )
            }

            result.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = it.message
                )
            }
        }
    }

    fun changeType(type: String) {

        if (type == _state.value.selectedType) return

        _state.value = _state.value.copy(
            selectedType = type,
            page = 0,
            hasMore = true,
            posts = emptyList()
        )

        loadPosts(reset = true)
    }

    fun loadMore() {
        loadPosts(reset = false)
    }
}