package com.example.myapplication.presentation.screen.promotion.promotion_detail

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
class PromotionDetailViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PromotionDetailState())
    val state: StateFlow<PromotionDetailState> = _state

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = repository.getPostById(postId)

            result.fold(
                onSuccess = {
                    _state.value = PromotionDetailState(
                        isLoading = false,
                        post = it
                    )
                },
                onFailure = {
                    _state.value = PromotionDetailState(
                        isLoading = false,
                        error = it.message
                    )
                }
            )
        }
    }
}